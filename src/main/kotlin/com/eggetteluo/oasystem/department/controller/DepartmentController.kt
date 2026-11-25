package com.eggetteluo.oasystem.department.controller

import com.eggetteluo.oasystem.common.response.ApiResponse
import com.eggetteluo.oasystem.department.dto.CreateDeptRequest
import com.eggetteluo.oasystem.department.dto.DepartmentTreeResponse
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

    @PostMapping("/create")
    fun create(@Valid @RequestBody request: CreateDeptRequest): ApiResponse<DeptResponse> {
        return ApiResponse.success(departmentService.createDept(request))
    }

    @GetMapping("/list")
    fun getAll(): ApiResponse<List<DeptResponse>> {
        return ApiResponse.success(departmentService.getAllDepts())
    }

    /**
     * 部门树形查询
     */
    @GetMapping("/tree")
    fun tree(): ApiResponse<List<DepartmentTreeResponse>> {
        return ApiResponse.success(departmentService.getDepartmentTree())
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: UUID): ApiResponse<DeptResponse> {
        return ApiResponse.success(departmentService.getDeptById(id))
    }

}