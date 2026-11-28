package com.eggetteluo.oasystem.user.service

import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.common.repository.EmployeeNoSequenceRepository
import com.eggetteluo.oasystem.common.service.EmployeeNoService
import com.eggetteluo.oasystem.department.repository.DepartmentRepository
import com.eggetteluo.oasystem.role.repository.RoleRepository
import com.eggetteluo.oasystem.user.dto.CreateUserRequest
import com.eggetteluo.oasystem.user.dto.UpdateUserRequest
import com.eggetteluo.oasystem.user.dto.UserResponse
import com.eggetteluo.oasystem.user.entity.UserEntity
import com.eggetteluo.oasystem.user.enums.UserStatus
import com.eggetteluo.oasystem.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
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
    private val roleRepository: RoleRepository,
    private val employeeNoService: EmployeeNoService,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun createUser(request: CreateUserRequest): UserResponse {
        // 校验唯一性
        if (userRepository.findByUsername(request.username) != null) {
            throw BusinessException(400, "该账户已存在")
        }

        // 手机号唯一
        request.phone?.let { phone ->
            if (userRepository.findByPhone(phone) != null) {
                throw BusinessException(400, "该手机号已被使用")
            }
        }

        // 校验部门
        val dept = departmentRepository.findById(request.departmentId)
            .orElseThrow { BusinessException(404, "部门不存在") }

        // 查询默认角色：普通员工
        val defaultRole = roleRepository.findByCode("EMPLOYEE")
            ?.takeIf { it.enabled }
            ?: throw BusinessException(500, "默认角色 EMPLOYEE 不存在或已禁用")

        // 获取默认角色 ID
        val roleId = defaultRole.id
            ?: throw BusinessException(500, "默认角色 ID 不存在")

        // 初始化工号年度
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
            department = dept,
            roleId = roleId
        )

        val saved = userRepository.save(user)
        return mapToResponse(saved)
    }

    @Transactional(readOnly = true)
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAllByDeletedAtIsNull().map { mapToResponse(it) }
    }

    /**
     * 查询当前登录用户资料
     */
    @Transactional(readOnly = true)
    fun getCurrentUser(username: String): UserResponse {
        val user = userRepository.findByUsername(username)
        if (user == null || user.deletedAt != null) {
            throw BusinessException(404, "用户不存在")
        }
        return mapToResponse(user)
    }

    /**
     * 更新用户资料
     */
    @Transactional
    fun updateUser(id: UUID, request: UpdateUserRequest): UserResponse {
        val user = getActiveUser(id)

        // 手机号唯一（排除自己）
        val phone = request.phone?.trim()?.takeIf { it.isNotEmpty() }
        if (request.phone != null) {
            phone?.let {
                val existing = userRepository.findByPhone(it)
                if (existing != null && existing.id != id) {
                    throw BusinessException(400, "该手机号已被使用")
                }
            }
        }

        // 部门校验
        val department = request.departmentId?.let {
            departmentRepository.findById(it)
                .orElseThrow { BusinessException(404, "部门不存在") }
        }

        // 角色校验
        val roleId = request.roleId?.takeIf { it != user.roleId }
        if (roleId != null) {
            val role = roleRepository.findByIdOrNull(roleId)
                ?: throw BusinessException(404, "角色不存在")
            if (!role.enabled || role.deletedAt != null) {
                throw BusinessException(400, "该角色已被禁用")
            }
        }

        user.realName = request.realName.trim()
        // 传 null 不修改，传空字符串清空
        if (request.email != null) {
            user.email = request.email.trim().takeIf { it.isNotEmpty() }
        }
        if (request.phone != null) {
            user.phone = phone
        }
        department?.let { user.department = it }
        roleId?.let { user.roleId = it }
        request.status?.let { user.status = it }

        return mapToResponse(userRepository.save(user))
    }

    /**
     * 删除用户（软删除）
     *
     * 只标记 deletedAt，保留历史数据；删除后无法登录，也无法再被查询到。
     */
    @Transactional
    fun deleteUser(id: UUID) {
        val user = getActiveUser(id)

        user.deletedAt = LocalDateTime.now()
        userRepository.save(user)
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
        departmentId: UUID,
        roleId: UUID
    ): String? {
        if (userRepository.existsByUsername("admin")) {
            return null
        }

        // 查询角色
        val role = roleRepository.findByIdOrNull(roleId) ?: throw BusinessException(400, "角色不存在")
        if (role.code != "ADMIN" || !role.enabled) {
            throw BusinessException(400, "超级管理员必须绑定有效的 ADMIN 角色")
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
            department = department,
            roleId = roleId
        )

        userRepository.save(adminUser)

        return employeeNo
    }

    /**
     * 查询未删除的用户，不存在或已删除统一按 404 处理
     */
    private fun getActiveUser(id: UUID): UserEntity {
        val user = userRepository.findByIdOrNull(id)
            ?: throw BusinessException(404, "用户不存在")
        if (user.deletedAt != null) {
            throw BusinessException(404, "用户不存在")
        }
        return user
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