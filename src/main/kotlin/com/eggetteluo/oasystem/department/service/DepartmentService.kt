package com.eggetteluo.oasystem.department.service

import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.department.dto.CreateDeptRequest
import com.eggetteluo.oasystem.department.dto.DeptResponse
import com.eggetteluo.oasystem.department.repository.DepartmentRepository
import com.eggetteluo.oasystem.department.entity.DepartmentEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DepartmentService(
    private val departmentRepository: DepartmentRepository
) {

    @Transactional
    fun createDept(request: CreateDeptRequest): DeptResponse {
        val parentDept = request.parentId?.let {
            departmentRepository.findById(it).orElseThrow { BusinessException(404, "上级部门不存在") }
        }

        val dept = DepartmentEntity(
            name = request.name,
            parent = parentDept,
            description = request.description
        )
        val saved = departmentRepository.save(dept)

        return DeptResponse(
            id = saved.id!!,
            name = saved.name,
            parentId = saved.parent?.id,
            parentName = saved.parent?.name
        )
    }

    fun getAllDepts(): List<DeptResponse> {
        return departmentRepository.findAll().map {
            DeptResponse(
                id = it.id!!,
                name = it.name,
                parentId = it.parent?.id,
                parentName = it.parent?.name
            )
        }
    }

    fun getDeptById(id: UUID): DeptResponse {
        val dept = departmentRepository.findById(id).orElseThrow { BusinessException(404, "部门不存在") }
        return DeptResponse(dept.id!!, dept.name, dept.parent?.id, dept.parent?.name)
    }

    @Transactional
    fun getOrCreateSystemDepartment(): UUID {
        val department = departmentRepository.findByName("系统管理部")
            ?: departmentRepository.save(
                DepartmentEntity(
                    name = "系统管理部",
                    description = "系统内置的最高权限部门"
                )
            )
        return department.id ?: throw IllegalStateException("系统管理部 ID 不存在")
    }

}