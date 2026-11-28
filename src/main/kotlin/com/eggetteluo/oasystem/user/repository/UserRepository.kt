package com.eggetteluo.oasystem.user.repository

import com.eggetteluo.oasystem.user.entity.UserEntity
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {

    // 登录时需要用账号查用户
    fun findByUsername(username: String): UserEntity?

    // 根据工号查用户
    fun findByEmployeeNo(employeeNo: String): UserEntity?

    // 手机号唯一，更新时用于校验重复
    fun findByPhone(phone: String): UserEntity?

    @EntityGraph(attributePaths = ["department"])
    override fun findAll(): List<UserEntity>

    // 查询未删除的用户
    @EntityGraph(attributePaths = ["department"])
    fun findAllByDeletedAtIsNull(): List<UserEntity>

    fun existsByUsername(username: String): Boolean

    fun countByRoleId(roleId: UUID): Long

    // 统计角色下未删除的用户，软删除的用户不占用角色
    fun countByRoleIdAndDeletedAtIsNull(roleId: UUID): Long

}