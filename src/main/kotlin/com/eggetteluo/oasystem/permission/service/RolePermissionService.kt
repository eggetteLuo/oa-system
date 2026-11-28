package com.eggetteluo.oasystem.permission.service

import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.permission.dto.PermissionResponse
import com.eggetteluo.oasystem.permission.dto.UpdateRolePermissionsRequest
import com.eggetteluo.oasystem.permission.entity.PermissionEntity
import com.eggetteluo.oasystem.permission.entity.RolePermissionEntity
import com.eggetteluo.oasystem.permission.repository.PermissionRepository
import com.eggetteluo.oasystem.permission.repository.RolePermissionRepository
import com.eggetteluo.oasystem.role.repository.RoleRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class RolePermissionService(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
    private val rolePermissionRepository: RolePermissionRepository
) {

    /**
     * 查询指定角色拥有的权限
     */
    @Transactional(readOnly = true)
    fun getRolePermissions(roleId: UUID): List<PermissionResponse> {
        // 查询角色是否存在
        val role = roleRepository.findByIdOrNull(roleId) ?: throw BusinessException(400, "角色不存在")

        // 查询角色权限关联记录
        val relations = rolePermissionRepository.findAllByRoleId(roleId)

        // 获取权限ID集合
        val permissionIds = relations.map { it.permissionId }.toSet()

        if (permissionIds.isEmpty()) {
            return emptyList()
        }

        // 根据权限 ID 批量查询权限信息
        return permissionRepository.findAllById(permissionIds)
            .sortedBy { it.code }
            .map { mapToResponse(it) }
    }

    /**
     * 整体替换角色权限
     */
    fun replaceRolePermissions(roleId: UUID, request: UpdateRolePermissionsRequest): List<PermissionResponse> {
        // 检查角色是否存在
        val role = roleRepository.findByIdOrNull(roleId) ?: throw BusinessException(404, "角色不存在")

        // 批量查询提交的权限
        val permissions = permissionRepository.findAllById(request.permissionIds).toList()

        // 检查提交的权限是否全部存在
        if (permissions.size != request.permissionIds.size) {
            throw BusinessException(404, "部分权限不存在，请刷新权限列表后重试")
        }

        // 检查权限是否全部启用
        val disabledPermission = permissions.firstOrNull { !it.enabled }
        if (disabledPermission != null) {
            throw BusinessException(400, "权限 ${disabledPermission.code} 已禁用，不能分配")
        }

        // 删除角色原本关联的权限
        rolePermissionRepository.deleteAllByRoleId(roleId)

        // 构建新的角色权限关联记录
        val relations = permissions.map { permission ->
            RolePermissionEntity(
                roleId = roleId,
                permissionId = permission.id ?: throw BusinessException(400, "权限 ID 不存在")
            )
        }

        // 保存新的权限关联
        if (relations.isNotEmpty()) {
            rolePermissionRepository.saveAll(relations)
        }

        // 返回更新后的权限列表
        return permissions.sortedBy { it.code }
            .map { mapToResponse(it) }
    }


    private fun mapToResponse(
        permission: PermissionEntity
    ): PermissionResponse {
        return PermissionResponse(
            id = permission.id!!,
            code = permission.code,
            name = permission.name,
            description = permission.description,
            enabled = permission.enabled,
            builtIn = permission.builtIn
        )
    }

}