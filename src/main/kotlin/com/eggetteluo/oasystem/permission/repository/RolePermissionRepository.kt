package com.eggetteluo.oasystem.permission.repository

import com.eggetteluo.oasystem.permission.entity.RolePermissionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RolePermissionRepository : JpaRepository<RolePermissionEntity, UUID> {

    fun findAllByRoleId(roleId: UUID): List<RolePermissionEntity>

    fun deleteAllByRoleId(roleId: UUID)

    fun existsByRoleIdAndPermissionId(
        roleId: UUID,
        permissionId: UUID
    ): Boolean

}