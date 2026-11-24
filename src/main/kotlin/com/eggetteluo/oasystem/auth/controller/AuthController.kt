package com.eggetteluo.oasystem.auth.controller

import com.eggetteluo.oasystem.auth.dto.LoginRequest
import com.eggetteluo.oasystem.auth.dto.LoginResponse
import com.eggetteluo.oasystem.auth.dto.TokenResponse
import com.eggetteluo.oasystem.auth.service.AuthService
import com.eggetteluo.oasystem.common.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ApiResponse<LoginResponse> {
        return ApiResponse.success("登录成功", authService.login(request))
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestHeader("Authorization") authHeader: String): ApiResponse<TokenResponse> {
        return ApiResponse.success("刷新成功", authService.refreshToken(authHeader))
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") authHeader: String): ApiResponse<Unit> {
        return ApiResponse.success("退出成功", authService.logout(authHeader))
    }

}