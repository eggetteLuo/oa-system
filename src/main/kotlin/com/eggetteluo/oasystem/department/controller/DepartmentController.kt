package com.eggetteluo.oasystem.department.controller

import com.eggetteluo.oasystem.department.dto.CreateDeptRequest
import com.eggetteluo.oasystem.department.dto.DeptResponse
import com.eggetteluo.oasystem.department.service.DepartmentService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/department")
class DepartmentController(
    private val departmentService: DepartmentService
) {

    @PostMapping
    fun create(@Valid @RequestBody request: CreateDeptRequest): Result<DeptResponse> {
        return Result.success(departmentService.createDept(request))
    }

    @GetMapping
    fun getAll(): Result<List<DeptResponse>> {
        return Result.success(departmentService.getAllDepts())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): Result<DeptResponse> {
        return Result.success(departmentService.getDeptById(id))
    }

}