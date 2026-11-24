package com.eggetteluo.oasystem.config

import com.eggetteluo.oasystem.department.entity.DepartmentEntity
import com.eggetteluo.oasystem.department.repository.DepartmentRepository
import com.eggetteluo.oasystem.user.entity.UserEntity
import com.eggetteluo.oasystem.user.enums.UserStatus
import com.eggetteluo.oasystem.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val departmentRepository: DepartmentRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(vararg args: String) {
        // 检查是否已经初始化
        if (userRepository.findByUsername("admin") != null) {
            log.info("系统已初始化，跳过超级管理员创建。")
            return
        }

        log.info("正在初始化超级管理员账号...")

        // 创建一个默认部门
        var adminDept = departmentRepository.findByName("系统管理部")
        if (adminDept == null) {
            adminDept = DepartmentEntity(
                name = "系统管理部",
                description = "系统内置的最高权限部门"
            )
            adminDept = departmentRepository.save(adminDept)
        }

        // 创建超管账号
        val adminUser = UserEntity(
            employeeNo = "EMP0001",
            username = "admin",
            password = passwordEncoder.encode("admin123") ?: "",
            realName = "超级管理员",
            status = UserStatus.ACTIVE,
            department = adminDept
        )

        userRepository.save(adminUser)
        log.info("超级管理员账号创建成功！账号: admin, 密码: admin123")
    }

}