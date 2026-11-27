package com.eggetteluo.oasystem.permission.service

import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.permission.dto.PermissionResponse
import com.eggetteluo.oasystem.permission.entity.PermissionEntity
import com.eggetteluo.oasystem.permission.repository.PermissionRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PermissionService(
    private val permissionRepository: PermissionRepository
) {

    /**
     * 查询权限列表
     */
    @Transactional(readOnly = true)
    fun list(): List<PermissionResponse> {
        return permissionRepository.findAll()
            .sortedBy { it.code }
            .map { mapToResponse(it) }
    }

    /**
     * 根据 ID 查询权限详情
     */
    @Transactional(readOnly = true)
    fun getById(id: UUID): PermissionResponse {
        val permission = permissionRepository.findByIdOrNull(id) ?: throw BusinessException(404, "权限不存在")
        return mapToResponse(permission)
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