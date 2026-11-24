package com.eggetteluo.oasystem.user

import com.eggetteluo.oasystem.common.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "sys_department")
class DepartmentEntity(

    @Column(nullable = false, length = 100)
    var name: String = "",

    @Column(length = 255)
    var description: String = "", // 部门描述

) : BaseEntity()