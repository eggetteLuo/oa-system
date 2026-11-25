package com.eggetteluo.oasystem.common.repository

import com.eggetteluo.oasystem.common.entity.EmployeeNoSequenceEntity
import io.lettuce.core.dynamic.annotation.Param
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface EmployeeNoSequenceRepository : JpaRepository<EmployeeNoSequenceEntity, Int> {

    /**
     * 查询指定年份的工号序列并加写锁。
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM EmployeeNoSequenceEntity s WHERE s.year = :year")
    fun findByYearForUpdate(@Param("year") year: Int): EmployeeNoSequenceEntity?

}