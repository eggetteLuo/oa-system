package com.eggetteluo.oasystem.common.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "employee_no_sequence")
class EmployeeNoSequenceEntity(

    @Id
    @Column(nullable = false)
    var year: Int,

    @Column(nullable = false)
    var currentValue: Long = 0

)