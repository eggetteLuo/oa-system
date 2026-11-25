package com.eggetteluo.oasystem.role.repository

import com.eggetteluo.oasystem.role.entity.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface RoleRepository : JpaRepository<RoleEntity, UUID> {

    /**
     * 根据角色编码查询
     */
    fun findByCode(code: String): RoleEntity?

    /**
     * 检查角色编码是否存在
     */
    fun existsByCode(code: String): Boolean

    /**
     * 按角色编码排序查询
     */
    fun findAllByOrderByCodeAsc(): List<RoleEntity>

}