package com.eggetteluo.oasystem.department.dto

import java.util.UUID

data class DepartmentTreeResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val parentId: UUID?,
    val sortOrder: Int,
    val leaderId: UUID?,
    val children: MutableList<DepartmentTreeResponse> = mutableListOf()
)
