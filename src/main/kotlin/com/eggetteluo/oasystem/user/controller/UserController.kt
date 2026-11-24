package com.eggetteluo.oasystem.user.controller

import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.common.result.Result
import com.eggetteluo.oasystem.user.entity.DepartmentEntity
import com.eggetteluo.oasystem.user.repository.DepartmentRepository
import com.eggetteluo.oasystem.user.entity.UserEntity
import com.eggetteluo.oasystem.user.repository.UserRepository
import com.eggetteluo.oasystem.user.dto.UserResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userRepository: UserRepository,
    private val departmentRepository: DepartmentRepository
) {

    // 创建部门
    @PostMapping("/dept")
    fun createDept(@RequestParam name: String): Result<DepartmentEntity> {
        // 部门名称重复，主动抛出异常
        if (departmentRepository.findByName(name) != null) {
            throw BusinessException(400, "部门【$name】已存在")
        }
        val dept = departmentRepository.save(DepartmentEntity(name = name))
        return Result.success(dept)
    }

    // 创建用户
    @PostMapping
    fun createUser(
        @RequestParam employeeNo: String,
        @RequestParam username: String,
        @RequestParam realName: String,
        @RequestParam deptId: UUID
    ): Result<UserEntity> {
        val dept = departmentRepository.findById(deptId)
            .orElseThrow { BusinessException(404, "部门不存在") }

        if (userRepository.findByUsername(username) != null) {
            throw BusinessException(400, "账号已存在")
        }

        val user = UserEntity(
            employeeNo = employeeNo,
            username = username,
            password = "123456",
            realName = realName,
            department = dept
        )
        return Result.success(userRepository.save(user))
    }

    // 获取所有用户
    @GetMapping
    fun getAllUsers(): Result<List<UserResponse>> {
        val users = userRepository.findAll().map { user ->
            UserResponse(
                id = user.id!!,
                employeeNo = user.employeeNo,
                username = user.username,
                realName = user.realName,
                status = user.status,
                departmentName = user.department?.name,
                createdAt = user.createdAt
            )
        }
        return Result.success(users)
    }

}