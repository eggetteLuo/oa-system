package com.eggetteluo.oasystem.permission.repository

import com.eggetteluo.oasystem.permission.entity.PermissionEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PermissionRepository : JpaRepository<PermissionEntity, UUID> {

    fun findByCode(code: String): PermissionEntity?

    fun existsByCode(code: String): Boolean

}