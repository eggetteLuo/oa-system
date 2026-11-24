package com.eggetteluo.oasystem.auth.service

import com.eggetteluo.oasystem.auth.util.JwtUtil
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.redis.core.SessionCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class TokenService(
    private val stringRedisTemplate: StringRedisTemplate,
    private val jwtUtil: JwtUtil
) {

    companion object {
        private const val KEY_PREFIX = "login:session:"

        // JWT 有效期
        private val TOKEN_EXPIRATION = Duration.ofHours(24)

        // 剩余 6 小时时触发刷新
        private val REFRESH_THRESHOLD = Duration.ofHours(6)
    }

    /**
     * 生成 Redis Key
     */
    private fun sessionKey(userId: UUID): String = "$KEY_PREFIX$userId"

    /**
     * 创建 Token
     */
    fun createToken(userId: UUID): String {
        val jti = UUID.randomUUID().toString()
        val token = jwtUtil.generateToken(userId, jti)

        stringRedisTemplate.opsForValue().set(sessionKey(userId), jti, TOKEN_EXPIRATION)

        return token
    }

    /**
     * 验证 JWT 签名、有效期和 Redis JTI
     */
    fun getUserIdByToken(token: String): UUID? {
        val payload = jwtUtil.parseToken(token) ?: return null
        val currentJti = stringRedisTemplate.opsForValue().get(sessionKey(payload.userId)) ?: return null
        return if (payload.jti == currentJti) payload.userId else null
    }

    /**
     * 刷新 Token
     */
    fun refreshToken(token: String): String? {
        val payload = jwtUtil.parseToken(token) ?: return null
        val key = sessionKey(payload.userId)

        // 确认当前 Token 是否有效
        val currentJti = stringRedisTemplate.opsForValue().get(key) ?: return null
        if (currentJti != payload.jti) {
            return null
        }

        // 计算剩余有效时间
        val remaining = Duration.between(
            Instant.now(),
            payload.expiresAt
        )

        // 有效时间为0或者已过期，不刷新
        if (remaining.isZero || remaining.isNegative) {
            return null
        }

        // 没有进入刷新窗口，不需要生成新 Token
        if (remaining > REFRESH_THRESHOLD) {
            return token
        }

        // 生成新的 Token
        val newJti = sessionKey(payload.userId)
        val newToken = jwtUtil.generateToken(payload.userId, newJti)

        // 替换 JTI
        val success = compareAndSetJti(
            userId = payload.userId,
            oldJti = payload.jti,
            newJti = newJti
        )

        return if (success) newToken else null
    }

    /**
     * 退出当前登录
     */
    fun deleteToken(token: String): Boolean {
        val payload = jwtUtil.parseToken(token) ?: return false

        return compareAndDeleteJti(
            userId = payload.userId,
            expectedJti = payload.jti
        )
    }

    /**
     * 原子比较并替换 JTI
     */
    fun compareAndSetJti(userId: UUID, oldJti: String, newJti: String): Boolean {
        val key = sessionKey(userId)

        val result = stringRedisTemplate.execute(
            object : SessionCallback<Boolean> {
                override fun <K : Any, V : Any> execute(operations: RedisOperations<K, V>): Boolean {
                    @Suppress("UNCHECKED_CAST")
                    val redis = operations as RedisOperations<String, String>

                    var transactionStarted = false
                    try {
                        // 监视 Key
                        redis.watch(key)

                        // 查询当前 JTI
                        val currentJti = stringRedisTemplate.opsForValue().get(sessionKey(userId))
                        if (currentJti != oldJti) {
                            redis.unwatch()
                            return false
                        }

                        // 开始事务
                        redis.multi()
                        transactionStarted = true

                        // 替换 JTI 并重新设置过期时间
                        redis.opsForValue().set(
                            key,
                            newJti,
                            TOKEN_EXPIRATION
                        )

                        // 提交事务
                        val results = redis.exec()
                        transactionStarted = false

                        return results.singleOrNull() == "OK" // 检测是否执行成功
                    } catch (e: RuntimeException) {
                        if (transactionStarted) {
                            runCatching { redis.discard() }
                        } else {
                            runCatching { redis.unwatch() }
                        }
                        throw e
                    }
                }
            }
        )

        return result == true
    }

    /**
     * 原子比较删除 JTI
     */
    fun compareAndDeleteJti(userId: UUID, expectedJti: String): Boolean {
        val key = sessionKey(userId)

        val result = stringRedisTemplate.execute(
            object : SessionCallback<Boolean> {
                override fun <K : Any, V : Any> execute(operations: RedisOperations<K, V>): Boolean {
                    @Suppress("UNCHECKED_CAST")
                    val redis = operations as RedisOperations<String, String>

                    var transactionStarted = false

                    try {
                        redis.watch(key)

                        val currentJti = stringRedisTemplate.opsForValue().get(sessionKey(userId))
                        if (currentJti != expectedJti) {
                            redis.unwatch()
                            return false
                        }

                        redis.multi()
                        transactionStarted = true

                        redis.delete(key)

                        val results = redis.exec()
                        transactionStarted = false

                        return results.singleOrNull() == 1L // 检测是否执行成功
                    } catch (e: RuntimeException) {
                        if (transactionStarted) {
                            runCatching { redis.discard() }
                        } else {
                            runCatching { redis.unwatch() }
                        }
                        throw e
                    }
                }
            }
        )

        return result == true
    }

}