package com.eggetteluo.oasystem.role.dto

import java.time.LocalDateTime
import java.util.UUID

data class RoleResponse(
    val id: UUID,
    val code: String,
    val name: String,
    val description: String?,
    val enabled: Boolean,
    val builtIn: Boolean,
    val userCount: Long,
    val createdAt: LocalDateTime?
)