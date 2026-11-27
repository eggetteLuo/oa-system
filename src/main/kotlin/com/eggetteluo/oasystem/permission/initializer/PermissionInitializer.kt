package com.eggetteluo.oasystem.permission.initializer

import com.eggetteluo.oasystem.permission.entity.PermissionEntity
import com.eggetteluo.oasystem.permission.repository.PermissionRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 系统预制权限初始化
 */
@Component
class PermissionInitializer(
    private val permissionRepository: PermissionRepository
) : ApplicationRunner {

    /**
     * 定义权限
     */
    private data class PermissionDefinition(
        val code: String,
        val name: String,
        val description: String
    )

    /**
     * 系统预置权限
     */
    private val permissions = listOf(

        // 用户管理
        PermissionDefinition(
            code = "user:create",
            name = "创建用户",
            description = "允许创建系统用户"
        ),
        PermissionDefinition(
            code = "user:read",
            name = "查看用户",
            description = "允许查看系统用户信息"
        ),
        PermissionDefinition(
            code = "user:update",
            name = "修改用户",
            description = "允许修改系统用户信息"
        ),
        PermissionDefinition(
            code = "user:delete",
            name = "删除用户",
            description = "允许删除系统用户"
        ),

        // 部门管理
        PermissionDefinition(
            code = "department:create",
            name = "创建部门",
            description = "允许创建部门"
        ),
        PermissionDefinition(
            code = "department:read",
            name = "查看部门",
            description = "允许查看部门信息"
        ),
        PermissionDefinition(
            code = "department:update",
            name = "修改部门",
            description = "允许修改部门信息"
        ),
        PermissionDefinition(
            code = "department:delete",
            name = "删除部门",
            description = "允许删除部门"
        ),

        // 角色管理
        PermissionDefinition(
            code = "role:create",
            name = "创建角色",
            description = "允许创建系统角色"
        ),
        PermissionDefinition(
            code = "role:read",
            name = "查看角色",
            description = "允许查看系统角色"
        ),
        PermissionDefinition(
            code = "role:update",
            name = "修改角色",
            description = "允许修改系统角色"
        ),
        PermissionDefinition(
            code = "role:delete",
            name = "删除角色",
            description = "允许删除系统角色"
        ),

        // 权限管理
        PermissionDefinition(
            code = "permission:read",
            name = "查看权限",
            description = "允许查看系统权限"
        ),
        PermissionDefinition(
            code = "role:permission:read",
            name = "查看角色权限",
            description = "允许查看角色已分配的权限"
        ),
        PermissionDefinition(
            code = "role:permission:assign",
            name = "分配角色权限",
            description = "允许为角色分配或修改权限"
        )
    )

    @Transactional
    override fun run(args: ApplicationArguments) {
        permissions.forEach { definition ->
            // 根据权限编码查询
            val existing = permissionRepository.findByCode(definition.code)

            if (existing == null) {
                val permission = PermissionEntity(
                    code = definition.code,
                    name = definition.name,
                    description = definition.description,
                    enabled = true,
                    builtIn = true
                )
                permissionRepository.save(permission)
            } else {
                // 已存在则同步名称和描述
                existing.name = definition.name
                existing.description = definition.description
                existing.builtIn = true
                permissionRepository.save(existing)
            }
        }
    }

}