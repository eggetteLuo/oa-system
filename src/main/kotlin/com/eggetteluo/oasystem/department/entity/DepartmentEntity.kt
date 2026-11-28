package com.eggetteluo.oasystem.department.entity

import com.eggetteluo.oasystem.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "sys_department")
class DepartmentEntity(

    @Column(nullable = false, length = 100)
    var name: String = "", // 部门名称

    @Column(length = 255)
    var description: String? = null, // 部门描述

    @Column(nullable = false)
    var sortOrder: Int = 0, // 用于排序

    @Column
    var leaderId: UUID? = null, // 部门负责人ID

    @Column(nullable = false)
    var enabled: Boolean = true, // 是否启用

    @Column
    var parentId: UUID? = null // 父级部门ID

) : BaseEntity()