package com.eggetteluo.oasystem.user.dto

import java.util.UUID

data class CreateUserRequest(
    val username: String,
    val password: String,
    val realName: String,
    val email: String? = null,
    val phone: String? = null,
    val departmentId: UUID
)
