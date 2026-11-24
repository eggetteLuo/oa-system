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

    @EntityGraph(attributePaths = ["department"])
    override fun findAll(): List<UserEntity>

}