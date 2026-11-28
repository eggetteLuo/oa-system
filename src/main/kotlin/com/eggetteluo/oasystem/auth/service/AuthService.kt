package com.eggetteluo.oasystem.auth.service

import com.eggetteluo.oasystem.auth.dto.LoginRequest
import com.eggetteluo.oasystem.auth.dto.LoginResponse
import com.eggetteluo.oasystem.auth.dto.TokenResponse
import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.user.enums.UserStatus
import com.eggetteluo.oasystem.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService
) {

    fun login(request: LoginRequest): LoginResponse {
        // 查用户是否存在（已软删除的用户等同不存在）
        val user = userRepository.findByUsername(request.username)
            ?.takeIf { it.deletedAt == null }
            ?: throw BusinessException(400, "账号或密码错误")

        // 校验密码是否正确
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BusinessException(400, "账号或密码错误")
        }

        // 校验状态
        if (user.status.name != UserStatus.ACTIVE.name) {
            throw BusinessException(403, "该账号已被禁用或离职")
        }

        // 生成 Token
        val token = tokenService.createToken(user.id!!)

        return LoginResponse(token, user.username, user.realName)
    }

    fun refreshToken(authHeader: String): TokenResponse {
        val oldToken = authHeader.removePrefix("Bearer ").trim()
        // 刷新 Token
        val newToken = tokenService.refreshToken(oldToken) ?: throw BusinessException(401, "登录已失效，请重新登录")
        return TokenResponse(newToken)
    }

    fun logout(authHeader: String) {
        val token = authHeader.removePrefix("Bearer ").trim()
        // 删除 Token
        val result = tokenService.deleteToken(token)
        if (!result) {
            throw BusinessException(401, "登录已失效，请重新登录")
        }
    }

}