package com.eggetteluo.oasystem.role.entity

import com.eggetteluo.oasystem.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "sys_role",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_sys_role_code",
            columnNames = ["code"]
        )
    ]
)
class RoleEntity(

    /**
     * 角色编码
     *
     * ADMIN / HR / MANAGER / EMPLOYEE
     */
    @Column(
        nullable = false,
        length = 50,
        updatable = false
    )
    var code: String = "",

    /**
     * 角色名称
     */
    @Column(nullable = false, length = 50)
    var name: String = "",

    /**
     * 角色描述
     */
    @Column(length = 255)
    var description: String? = null,

    /**
     * 是否启用
     */
    @Column(nullable = false)
    var enabled: Boolean = true,

    /**
     * 是否为系统内置角色
     */
    @Column(nullable = false)
    var builtIn: Boolean = false

) : BaseEntity()