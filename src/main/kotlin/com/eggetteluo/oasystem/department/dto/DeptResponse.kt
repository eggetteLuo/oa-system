package com.eggetteluo.oasystem.department.dto

import java.util.UUID

data class DeptResponse(
    val id: UUID,
    val name: String,
    val parentId: UUID?,
    val parentName: String?
)
