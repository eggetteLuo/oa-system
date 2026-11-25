package com.eggetteluo.oasystem.role.entity

import com.eggetteluo.oasystem.common.BaseEntity
import com.eggetteluo.oasystem.role.enums.PermissionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "sys_permissions")
class PermissionEntity(

    @Column(nullable = false, unique = true, length = 100)
    var code: String = "", // dept:create / leave:approve

    @Column(nullable = false, length = 50)
    var name: String = "", // 新增部门 / 审批请假

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var type: PermissionType = PermissionType.BUTTON, // 权限类型

    @Column
    var parentId: UUID? = null, // 菜单树

    var path: String? = null,        // 菜单路由
    var component: String? = null,   // 菜单组件
    var icon: String? = null,        // 菜单图标

    @Column(nullable = false)
    var sortOrder: Int = 0,

    @Column(nullable = false)
    var enabled: Boolean = true

) : BaseEntity()