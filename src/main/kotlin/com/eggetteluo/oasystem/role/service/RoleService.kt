package com.eggetteluo.oasystem.role.service

import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.role.dto.CreateRoleRequest
import com.eggetteluo.oasystem.role.dto.RoleResponse
import com.eggetteluo.oasystem.role.dto.UpdateRoleRequest
import com.eggetteluo.oasystem.role.entity.RoleEntity
import com.eggetteluo.oasystem.role.repository.RoleRepository
import com.eggetteluo.oasystem.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RoleService(
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository
) {

    companion object {
        private val ROLE_CODE_PATTERN = Regex("^[A-Z][A-Z0-9_]{0,49}$")
    }

    /**
     * 创建角色
     */
    @Transactional
    fun create(request: CreateRoleRequest): RoleResponse {
        // 统一角色编码格式
        val code = request.code.trim().uppercase(Locale.ROOT)

        // 检查编码是否合法
        if (!ROLE_CODE_PATTERN.matches(code)) {
            throw BusinessException(400, "角色编码格式不正确")
        }

        // 创建角色
        val role = RoleEntity(
            code = code,
            name = request.name,
            description = request.description?.trim(),
            enabled = true,
            builtIn = false
        )

        val saved = roleRepository.save(role)

        return toResponse(saved)
    }

    /**
     * 查询角色列表
     */
    @Transactional(readOnly = true)
    fun list(): List<RoleResponse> {

        return roleRepository.findAllByOrderByCodeAsc()
            .map { toResponse(it) }
    }

    /**
     * 查询角色详情
     */
    @Transactional(readOnly = true)
    fun getById(id: UUID): RoleResponse {
        val role = roleRepository.findByIdOrNull(id) ?: throw BusinessException(404, "角色不存在")
        return toResponse(role)
    }

    /**
     * 更新角色
     */
    @Transactional
    fun update(id: UUID, request: UpdateRoleRequest): RoleResponse {
        val role = roleRepository.findByIdOrNull(id) ?: throw BusinessException(404, "角色不存在")

        // 超级管理员不能被禁用
        if (role.code == "ADMIN" && !request.enabled) {
            throw BusinessException(400, "超级管理员角色不允许禁用")
        }

        role.name = request.name.trim()
        role.description = request.description?.trim()
        role.enabled = request.enabled

        return toResponse(roleRepository.save(role))
    }

    /**
     * 删除角色
     */
    @Transactional
    fun delete(id: UUID) {
        val role = roleRepository.findByIdOrNull(id) ?: throw BusinessException(404, "角色不存在")

        if (role.builtIn) {
            throw BusinessException(400, "内置角色不允许删除")
        }

        // 检查是否有用户绑定该角色（已软删除的用户不计入）
        val userCount = userRepository.countByRoleIdAndDeletedAtIsNull(id)
        if (userCount > 0) {
            throw BusinessException(400, "该角色下还有 $userCount 个用户，无法删除")
        }

        roleRepository.delete(role)
    }

    /**
     * 根据角色编码获取角色 ID
     */
    @Transactional(readOnly = true)
    fun getRoleIdByCode(code: String): UUID {
        val role = roleRepository.findByCode(code)
            ?: throw BusinessException(500, "系统角色不存在：$code")
        if (!role.enabled) {
            throw BusinessException(500, "系统角色已被禁用：$code")
        }
        return requireNotNull(role.id)
    }

    /**
     * Entity 转 Response
     */
    private fun toResponse(role: RoleEntity): RoleResponse {
        val roleId = requireNotNull(role.id) {
            "角色 ID 尚未生成"
        }

        return RoleResponse(
            id = roleId,
            code = role.code,
            name = role.name,
            description = role.description,
            enabled = role.enabled,
            builtIn = role.builtIn,
            userCount = userRepository.countByRoleIdAndDeletedAtIsNull(roleId),
            createdAt = role.createdAt
        )
    }

}