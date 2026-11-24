package com.eggetteluo.oasystem.auth.service

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TokenService(
    private val stringRedisTemplate: StringRedisTemplate
) {

    private val tokenExpiration = Duration.ofHours(24)

    /**
     * 保存Token并互斥登录
     */
    fun saveToken(userId: String, token: String) {
        val userKey = "login:user:$userId"
        val tokenKey = "login:token:$token"

        // 查找该用户是否正在登录
        val oldToken = stringRedisTemplate.opsForValue().get(userKey)

        // 如果用户正在登录，则把旧Token从Redis中删除
        if (oldToken != null) {
            stringRedisTemplate.delete("login:token:$oldToken")
        }

        // 存入新的对应关系，并设置过期时间
        stringRedisTemplate.opsForValue().set(userKey, token, tokenExpiration)
        stringRedisTemplate.opsForValue().set(tokenKey, userId, tokenExpiration)
    }

    /**
     * 校验Token是否有效
     */
    fun isTokenValid(token: String): Boolean {
        return stringRedisTemplate.hasKey("login:token:$token") == true
    }

    /**
     * 退出登录，清除 Redis 记录
     */
    fun deleteToken(userId: String, token: String) {
        stringRedisTemplate.delete("login:user:$userId")
        stringRedisTemplate.delete("login:token:$token")
    }

    /**
     * 获取当前在线用户数
     */
    fun getOnlineUserCount(): Long {
        val keys = stringRedisTemplate.keys("login:user:*")
        return keys?.size?.toLong() ?: 0L
    }

}