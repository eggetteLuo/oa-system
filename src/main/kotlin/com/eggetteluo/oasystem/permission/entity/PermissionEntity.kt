package com.eggetteluo.oasystem.permission.entity

import com.eggetteluo.oasystem.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

/**
 * 系统权限实体
 */
@Entity
@Table(name = "sys_permission")
class PermissionEntity(

    /**
     * 权限编码
     * user:create / user:read / user:update
     */
    @Column(
        nullable = false,
        length = 100,
        updatable = false,
        unique = true
    )
    var code: String = "",

    /**
     * 权限名称
     */
    @Column(nullable = false, length = 50)
    var name: String = "",

    /**
     * 权限描述
     */
    @Column(length = 255)
    var description: String? = null,

    /**
     * 是否启用
     */
    @Column(nullable = false)
    var enabled: Boolean = true,

    /**
     * 是否系统内置权限
     */
    @Column(nullable = false)
    var builtIn: Boolean = false

) : BaseEntity()