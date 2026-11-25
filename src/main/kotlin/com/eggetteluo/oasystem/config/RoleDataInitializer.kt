package com.eggetteluo.oasystem.role.config

import com.eggetteluo.oasystem.role.entity.RoleEntity
import com.eggetteluo.oasystem.role.repository.RoleRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Order(1)
class RoleDataInitializer(
    private val roleRepository: RoleRepository
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional
    override fun run(vararg args: String) {

        log.info("开始检查系统内置角色...")

        val defaultRoles = listOf(
            RoleEntity(
                code = "ADMIN",
                name = "超级管理员",
                description = "拥有系统最高管理权限",
                builtIn = true
            ),
            RoleEntity(
                code = "HR",
                name = "人事",
                description = "负责员工和人事相关业务",
                builtIn = true
            ),
            RoleEntity(
                code = "MANAGER",
                name = "部门经理",
                description = "负责本部门管理和业务审批",
                builtIn = true
            ),
            RoleEntity(
                code = "EMPLOYEE",
                name = "普通员工",
                description = "普通员工基础角色",
                builtIn = true
            )
        )

        var createdCount = 0

        defaultRoles.forEach { defaultRole ->

            val existing = roleRepository.findByCode(defaultRole.code)

            if (existing == null) {

                roleRepository.save(defaultRole)
                createdCount++

                log.info(
                    "创建内置角色：{} ({})",
                    defaultRole.name,
                    defaultRole.code
                )

            } else if (!existing.builtIn) {

                // 确保系统预置角色具有 builtIn 标记
                existing.builtIn = true
                roleRepository.save(existing)
            }
        }

        log.info(
            "系统内置角色检查完成，本次新增 {} 个角色。",
            createdCount
        )
    }
}