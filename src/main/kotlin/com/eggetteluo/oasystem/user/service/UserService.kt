package com.eggetteluo.oasystem.user.service

import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.department.repository.DepartmentRepository
import com.eggetteluo.oasystem.user.dto.CreateUserRequest
import com.eggetteluo.oasystem.user.dto.UserResponse
import com.eggetteluo.oasystem.user.entity.UserEntity
import com.eggetteluo.oasystem.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val departmentRepository: DepartmentRepository,
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

        // 构建并保存用户
        val user = UserEntity(
            employeeNo = request.employeeNo,
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

    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { mapToResponse(it) }
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