package com.eggetteluo.oasystem.permission.controller

import com.eggetteluo.oasystem.common.response.ApiResponse
import com.eggetteluo.oasystem.permission.dto.PermissionResponse
import com.eggetteluo.oasystem.permission.dto.UpdateRolePermissionsRequest
import com.eggetteluo.oasystem.permission.service.RolePermissionService
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * 角色权限管理控制器
 */
@RestController
@RequestMapping("/api/role/{roleId}/permissions")
class RolePermissionController(
    private val rolePermissionService: RolePermissionService
) {

    /**
     * 查询角色拥有的权限
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('role:permission:read')")
    fun getRolePermissions(
        @PathVariable roleId: UUID
    ): ApiResponse<List<PermissionResponse>> {
        return ApiResponse.success(
            rolePermissionService.getRolePermissions(roleId)
        )
    }

    /**
     * 整体替换角色权限
     */
    @PutMapping("/update")
    @PreAuthorize("hasAuthority('role:permission:assign')")
    fun replaceRolePermissions(
        @PathVariable roleId: UUID,
        @Valid @RequestBody request: UpdateRolePermissionsRequest
    ): ApiResponse<List<PermissionResponse>> {
        return ApiResponse.success(
            rolePermissionService.replaceRolePermissions(roleId, request)
        )
    }

}