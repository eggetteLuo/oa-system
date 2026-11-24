package com.eggetteluo.oasystem.user.controller

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
    fun createUser(@Valid @RequestBody request: CreateUserRequest): Result<UserResponse> {
        return Result.success(userService.createUser(request))
    }

    @GetMapping
    fun getAllUsers(): Result<List<UserResponse>> {
        return Result.success(userService.getAllUsers())
    }
}