package com.eggetteluo.oasystem.department.entity

import com.eggetteluo.oasystem.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "sys_department")
class DepartmentEntity(

    @Column(nullable = false, length = 100)
    var name: String = "", // 部门名称

    @Column(length = 255)
    var description: String? = null, // 部门描述

    @Column(nullable = false)
    var sortOrder: Int = 0, // 用于排序

    // 自关联：多对一
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent: DepartmentEntity? = null

) : BaseEntity()