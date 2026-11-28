package com.eggetteluo.oasystem.user.controller

import com.eggetteluo.oasystem.common.response.ApiResponse
import com.eggetteluo.oasystem.user.dto.CreateUserRequest
import com.eggetteluo.oasystem.user.dto.UpdateUserRequest
import com.eggetteluo.oasystem.user.dto.UserResponse
import com.eggetteluo.oasystem.user.service.UserService
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    /**
     * 创建用户
     */
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ApiResponse<UserResponse> {
        return ApiResponse.success(userService.createUser(request))
    }

    /**
     * 查询用户列表
     */
    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    fun getAllUsers(): ApiResponse<List<UserResponse>> {
        return ApiResponse.success(userService.getAllUsers())
    }

    /**
     * 查询当前登录用户资料
     */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('user:profile:read')")
    fun getCurrentUser(authentication: Authentication): ApiResponse<UserResponse> {
        return ApiResponse.success(userService.getCurrentUser(authentication.name))
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    fun updateUser(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateUserRequest
    ): ApiResponse<UserResponse> {
        return ApiResponse.success(userService.updateUser(id, request))
    }

    /**
     * 删除用户（软删除）
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    fun deleteUser(
        @PathVariable id: UUID
    ): ApiResponse<Unit> {
        userService.deleteUser(id)
        return ApiResponse.success(Unit)
    }

}