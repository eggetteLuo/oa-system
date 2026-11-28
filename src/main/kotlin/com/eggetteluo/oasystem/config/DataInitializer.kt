package com.eggetteluo.oasystem.config

import com.eggetteluo.oasystem.common.service.EmployeeNoService
import com.eggetteluo.oasystem.role.repository.RoleRepository
import com.eggetteluo.oasystem.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.UUID

/**
 * 系统初始化器
 */
@Component
@Order(100)
class DataInitializer(
    private val userService: UserService,
    private val employeeNoService: EmployeeNoService,
    private val roleRepository: RoleRepository,

    @Value($$"${app.initial-admin.enabled:true}")
    private val adminEnabled: Boolean,

    @Value($$"${app.initial-admin.password:}")
    private val adminPassword: String,

    @Value($$"${app.initial-admin.department-id:00000000-0000-0000-0000-000000000001}")
    private val departmentId: UUID
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(vararg args: String) {

        // 初始化当前年度工号序列
        val currentYear = LocalDate.now().year

        employeeNoService.initializeYear(currentYear)

        log.info("已检查 {} 年工号序列。", currentYear)

        // 检查是否开启超级管理员初始化
        if (!adminEnabled) {
            log.info("超级管理员自动初始化已关闭。")
            return
        }

        // 检查超级管理员是否已经存在
        if (userService.existsByUsername("admin")) {
            log.info("超级管理员已存在，跳过初始化。")
            return
        }

        // 检查初始密码
        require(adminPassword.isNotBlank()) {
            "请通过 INITIAL_ADMIN_PASSWORD 环境变量配置超级管理员初始密码"
        }

        log.info("正在初始化超级管理员账号...")

        // 查询 ADMIN 角色
        val adminRole = roleRepository.findByCode("ADMIN")
            ?: throw IllegalStateException("系统内置 ADMIN 角色不存在")

        val adminRoleId = adminRole.id
            ?: throw IllegalStateException("ADMIN 角色 ID 不存在")

        // 创建超级管理员
        val employeeNo = userService.createInitialAdmin(
            password = adminPassword,
            departmentId = departmentId,
            roleId = adminRoleId
        )

        if (employeeNo != null) {
            log.info(
                "超级管理员创建成功！账号: admin, 工号: {}",
                employeeNo
            )
        } else {
            log.info("超级管理员已存在，跳过创建。")
        }
    }
}
