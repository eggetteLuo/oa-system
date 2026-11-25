package com.eggetteluo.oasystem.common.service

import com.eggetteluo.oasystem.common.entity.EmployeeNoSequenceEntity
import com.eggetteluo.oasystem.common.exception.BusinessException
import com.eggetteluo.oasystem.common.repository.EmployeeNoSequenceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EmployeeNoService(
    private val employeeNoSequenceRepository: EmployeeNoSequenceRepository
) {

    /**
     * 初始化指定年份的工号序列
     */
    @Transactional
    fun initializeYear(year: Int) {
        if (!employeeNoSequenceRepository.existsById(year)) {
            employeeNoSequenceRepository.saveAndFlush(
                EmployeeNoSequenceEntity(
                    year = year,
                    currentValue = 0
                )
            )
        }
    }

    /**
     * 生成工号
     *
     * 使用数据库悲观锁，保证并发安全
     */
    @Transactional
    fun generateEmployeeNo(): String {
        val year = LocalDateTime.now().year
        val sequence = employeeNoSequenceRepository.findByYearForUpdate(year)
            ?: throw BusinessException(500, "$year 年的工号尚未初始化")
        val nextValue = sequence.currentValue + 1
        if (nextValue > 9999) {
            throw BusinessException(500, "$year 年的工号已达到上限")
        }
        sequence.currentValue = nextValue
        return "EMP$year${nextValue.toString().padStart(4, '0')}"
    }

}