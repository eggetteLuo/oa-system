package com.eggetteluo.oasystem.department.repository

import com.eggetteluo.oasystem.department.entity.DepartmentEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DepartmentRepository : JpaRepository<DepartmentEntity, UUID> {

    fun findByName(name: String): DepartmentEntity?

}