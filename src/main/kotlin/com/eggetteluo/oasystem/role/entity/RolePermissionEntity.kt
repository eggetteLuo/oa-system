package com.eggetteluo.oasystem.role.entity

import com.eggetteluo.oasystem.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID

@Entity
@Table(
    name = "sys_role_permissions",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_role_permission", columnNames = ["role_id", "permission_id"])
    ]
)
class RolePermissionEntity(

    @Column(name = "role_id", nullable = false)
    var roleId: UUID = UUID.randomUUID(),

    @Column(name = "permission_id", nullable = false)
    var permissionId: UUID = UUID.randomUUID()

) : BaseEntity()