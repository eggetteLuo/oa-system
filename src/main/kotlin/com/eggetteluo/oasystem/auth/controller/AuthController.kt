package com.eggetteluo.oasystem.auth.controller

import com.eggetteluo.oasystem.auth.dto.LoginRequest
import com.eggetteluo.oasystem.auth.dto.LoginResponse
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
        return ApiResponse.success(authService.login(request))
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") authHeader: String): ApiResponse<Unit> {
        return ApiResponse.success(authService.logout(authHeader))
    }

}