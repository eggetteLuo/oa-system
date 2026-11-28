package com.eggetteluo.oasystem.permission.entity

import com.eggetteluo.oasystem.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID

/**
 * 角色权限关联实体
 */
@Entity
@Table(
    name = "sys_role_permission",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_sys_role_permission",
            columnNames = ["role_id", "permission_id"]
        )
    ]
)
class RolePermissionEntity(

    /**
     * 角色 ID
     */
    @Column(
        name = "role_id",
        nullable = false,
        updatable = false
    )
    var roleId: UUID = UUID(0, 0),

    /**
     * 权限 ID
     */
    @Column(
        name = "permission_id",
        nullable = false,
        updatable = false
    )
    var permissionId: UUID = UUID(0, 0)

) : BaseEntity()