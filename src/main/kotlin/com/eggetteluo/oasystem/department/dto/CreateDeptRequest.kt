package com.eggetteluo.oasystem.department.dto

import jakarta.validation.constraints.NotBlank
import java.util.UUID

data class CreateDeptRequest(
    @field:NotBlank
    val name: String,
    val description: String = "",
    val parentId: UUID? = null // 传入id之后建立子部门，形成树状结构
)
