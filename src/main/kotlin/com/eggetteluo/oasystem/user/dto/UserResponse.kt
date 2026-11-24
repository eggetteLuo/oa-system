package com.eggetteluo.oasystem.user.dto

import com.eggetteluo.oasystem.user.enums.UserStatus
import java.time.LocalDateTime
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val employeeNo: String,
    val username: String,
    val realName: String,
    val email: String? = null,
    val phone: String? = null,
    val status: UserStatus,
    val departmentName: String?,
    val createdAt: LocalDateTime?
)