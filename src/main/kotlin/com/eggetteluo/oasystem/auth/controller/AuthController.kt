package com.eggetteluo.oasystem.auth.controller

import com.eggetteluo.oasystem.auth.dto.LoginRequest
import com.eggetteluo.oasystem.auth.dto.LoginResponse
import com.eggetteluo.oasystem.auth.util.JwtUtil
import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.user.repository.UserRepository
import jakarta.validation.Valid
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): Result<LoginResponse> {
        // 查用户
        val user = userRepository.findByUsername(request.username)
            ?: throw BusinessException(401, "账号或密码错误")

        // 校验密码
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BusinessException(401, "账号或密码错误")
        }

        // 校验状态
        if (user.status.name != "ACTIVE") {
            throw BusinessException(403, "该账号已被禁用或离职")
        }

        // 生成 Token
        val token = jwtUtil.generateToken(user.username)

        return Result.success(LoginResponse(token, user.username, user.realName))
    }

}