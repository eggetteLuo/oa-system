package com.eggetteluo.oasystem.department.repository

import com.eggetteluo.oasystem.department.entity.DepartmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DepartmentRepository : JpaRepository<DepartmentEntity, UUID> {

    fun findByName(name: String): DepartmentEntity?

    fun findAllByOrderBySortOrderAscNameAsc(): List<DepartmentEntity>

    fun findAllByParentId(parentId: UUID?): List<DepartmentEntity>

    fun existsByParentId(parentId: UUID): Boolean

    fun existsByNameAndParentId(name: String, parentId: UUID?): Boolean

}