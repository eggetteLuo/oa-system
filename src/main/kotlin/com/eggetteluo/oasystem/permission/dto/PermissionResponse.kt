package com.eggetteluo.oasystem.permission.dto

import java.util.UUID

data class PermissionResponse(
    val id: UUID,
    val code: String,
    val name: String,
    val description: String?,
    val enabled: Boolean,
    val builtIn: Boolean
)
