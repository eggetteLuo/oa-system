package com.eggetteluo.oasystem.user.service

import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.common.repository.EmployeeNoSequenceRepository
import com.eggetteluo.oasystem.common.service.EmployeeNoService
import com.eggetteluo.oasystem.department.repository.DepartmentRepository
import com.eggetteluo.oasystem.user.dto.CreateUserRequest
import com.eggetteluo.oasystem.user.dto.UserResponse
import com.eggetteluo.oasystem.user.entity.UserEntity
import com.eggetteluo.oasystem.user.enums.UserStatus
import com.eggetteluo.oasystem.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val departmentRepository: DepartmentRepository,
    private val employeeNoService: EmployeeNoService,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun createUser(request: CreateUserRequest): UserResponse {
        // 校验唯一性
        if (userRepository.findByUsername(request.username) != null) {
            throw BusinessException(400, "该账户已存在")
        }

        // 校验部门
        val dept = departmentRepository.findById(request.departmentId)
            .orElseThrow { BusinessException(404, "部门不存在") }

        val year = LocalDateTime.now().year
        employeeNoService.initializeYear(year)

        // 构建并保存用户
        val user = UserEntity(
            employeeNo = employeeNoService.generateEmployeeNo(),
            username = request.username,
            password = passwordEncoder.encode(request.password) ?: "", // 核心：加密
            realName = request.realName,
            email = request.email,
            phone = request.phone,
            department = dept
        )

        val saved = userRepository.save(user)
        return mapToResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { mapToResponse(it) }
    }

    /**
     * 查询用户是否存在
     */
    fun existsByUsername(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    /**
     * 创建超级管理员用户
     */
    @Transactional
    fun createInitialAdmin(
        password: String,
        departmentId: UUID
    ): String? {
        if (userRepository.existsByUsername("admin")) {
            return null
        }

        // 查询所属部门
        val department = departmentRepository.findById(departmentId)
            .orElseThrow {
                BusinessException(404, "系统管理部不存在")
            }

        // 自动生成年度工号
        val employeeNo = employeeNoService.generateEmployeeNo()

        // 创建超级管理员
        val adminUser = UserEntity(
            employeeNo = employeeNo,
            username = "admin",
            password = passwordEncoder.encode(password) ?: "",
            realName = "超级管理员",
            status = UserStatus.ACTIVE,
            department = department
        )

        userRepository.save(adminUser)

        return employeeNo
    }

    private fun mapToResponse(user: UserEntity): UserResponse {
        return UserResponse(
            id = user.id!!,
            employeeNo = user.employeeNo,
            username = user.username,
            realName = user.realName,
            email = user.email,
            phone = user.phone,
            status = user.status,
            departmentName = user.department?.name,
            createdAt = user.createdAt
        )
    }

}