package com.eggetteluo.oasystem.permission.controller

import com.eggetteluo.oasystem.common.response.ApiResponse
import com.eggetteluo.oasystem.permission.dto.PermissionResponse
import com.eggetteluo.oasystem.permission.service.PermissionService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/api/permission")
class PermissionController(
    private val permissionService: PermissionService
) {

    /**
     * 查询权限列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('permission:read')")
    fun getPermissionList(): ApiResponse<List<PermissionResponse>> {
        return ApiResponse.success(permissionService.list())
    }

    /**
     * 查询权限详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:read')")
    fun getPermission(
        @PathVariable id: UUID
    ): ApiResponse<PermissionResponse> {
        return ApiResponse.success(permissionService.getById(id))
    }

}