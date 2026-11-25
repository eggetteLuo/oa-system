package com.eggetteluo.oasystem.user.entity

import com.eggetteluo.oasystem.common.BaseEntity
import com.eggetteluo.oasystem.department.entity.DepartmentEntity
import com.eggetteluo.oasystem.user.enums.UserStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "sys_user")
class UserEntity(

    @Column(nullable = false, unique = true, length = 20, updatable = false)
    var employeeNo: String = "", // 工号

    @Column(nullable = false, unique = true, length = 50)
    var username: String = "", // 账号

    @Column(nullable = false)
    var password: String = "", // 密码

    @Column(nullable = false, length = 50)
    var realName: String = "", // 真名，昵称

    @Column(length = 100)
    var email: String? = null, // 邮箱

    @Column(length = 20, unique = true)
    var phone: String? = null, // 手机号，唯一

    // 枚举类型默认存为整数，加上 STRING 后会在数据库存为 'ACTIVE' 这样的字符串，可读性更好
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: UserStatus = UserStatus.ACTIVE, // 用户状态

    @Column(name = "role_id")
    var roleId: UUID, // 角色Id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    var department: DepartmentEntity? = null // 关联的部门

) : BaseEntity()