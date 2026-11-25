package com.eggetteluo.oasystem.config

import com.eggetteluo.oasystem.common.service.EmployeeNoService
import com.eggetteluo.oasystem.department.service.DepartmentService
import com.eggetteluo.oasystem.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class DataInitializer(
    private val userService: UserService,
    private val departmentService: DepartmentService,
    private val employeeNoService: EmployeeNoService
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(vararg args: String) {

        // 初始化当前年度工号序列
        val currentYear = LocalDate.now().year

        employeeNoService.initializeYear(currentYear)

        log.info("已检查 {} 年工号序列。", currentYear)

        // 检查超级管理员是否存在
        if (userService.existsByUsername("admin")) {
            log.info("系统已初始化，跳过超级管理员创建。")
            return
        }

        log.info("正在初始化超级管理员账号...")

        // 获取或创建系统管理部
        val departmentId = departmentService.getOrCreateSystemDepartment()

        // 创建超级管理员
        val employeeNo = userService.createInitialAdmin(
            password = "admin123",
            departmentId = departmentId
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