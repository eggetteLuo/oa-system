package com.eggetteluo.oasystem.auth.service

import com.eggetteluo.oasystem.auth.dto.LoginRequest
import com.eggetteluo.oasystem.auth.dto.LoginResponse
import com.eggetteluo.oasystem.auth.util.JwtUtil
import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.user.enums.UserStatus
import com.eggetteluo.oasystem.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil,
    private val tokenService: TokenService
) {

    fun login(request: LoginRequest): LoginResponse {
        // 查用户是否存在
        val user = userRepository.findByUsername(request.username) ?: throw BusinessException(400, "账号或密码错误")

        // 校验密码是否正确
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BusinessException(400, "账号或密码错误")
        }

        // 校验状态
        if (user.status.name != UserStatus.ACTIVE.name) {
            throw BusinessException(403, "该账号已被禁用或离职")
        }

        // 生成 Token
        val token = jwtUtil.generateToken(user.username)

        // 存入 Redis 实现单点登录
        tokenService.saveToken(user.id.toString(), token)

        return LoginResponse(token, user.username, user.realName)
    }

    fun logout(authHeader: String) {
        val token = authHeader.removePrefix("Bearer ").trim()
        val username = jwtUtil.extractUsername(token)

        if (username != null) {
            val user = userRepository.findByUsername(username)
            if (user != null) {
                // 从 Redis 中删除 Token
                tokenService.deleteToken(user.id.toString(), token)
            }
        }
    }

}