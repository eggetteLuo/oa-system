package com.eggetteluo.oasystem.user.controller

import com.eggetteluo.oasystem.common.response.ApiResponse
import com.eggetteluo.oasystem.user.dto.CreateUserRequest
import com.eggetteluo.oasystem.user.dto.UserResponse
import com.eggetteluo.oasystem.user.service.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ApiResponse<UserResponse> {
        return ApiResponse.success(userService.createUser(request))
    }

    @GetMapping
    fun getAllUsers(): ApiResponse<List<UserResponse>> {
        return ApiResponse.success(userService.getAllUsers())
    }
}