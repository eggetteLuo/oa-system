package com.eggetteluo.oasystem.role.controller

import com.eggetteluo.oasystem.common.response.ApiResponse
import com.eggetteluo.oasystem.role.dto.CreateRoleRequest
import com.eggetteluo.oasystem.role.dto.RoleResponse
import com.eggetteluo.oasystem.role.dto.UpdateRoleRequest
import com.eggetteluo.oasystem.role.service.RoleService
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/role")
@PreAuthorize("hasRole('ADMIN')")
class RoleController(
    private val roleService: RoleService
) {

    /**
     * 创建角色
     */
    @PostMapping("/create")
    fun createRole(@Valid @RequestBody request: CreateRoleRequest): ApiResponse<RoleResponse> {
        return ApiResponse.success(roleService.create(request))
    }

    /**
     * 查询角色列表
     */
    @GetMapping("/list")
    fun getRoleList(): ApiResponse<List<RoleResponse>> {
        return ApiResponse.success(roleService.list())
    }

    /**
     * 查询角色详情
     */
    @GetMapping("/{id}")
    fun getRole(@PathVariable id: UUID): ApiResponse<RoleResponse> {
        return ApiResponse.success(roleService.getById(id))
    }

    /**
     * 更新角色
     */
    @PutMapping("/update/{id}")
    fun updateRole(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateRoleRequest
    ): ApiResponse<RoleResponse> {
        return ApiResponse.success(roleService.update(id, request))
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ApiResponse<Unit> {
        roleService.delete(id)
        return ApiResponse.success(Unit)
    }

}