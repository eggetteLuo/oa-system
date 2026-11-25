package com.eggetteluo.oasystem.role.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateRoleRequest(

    @field:NotBlank(message = "角色编码不能为空")
    @field:Size(max = 50, message = "角色编码不能超过50个字符")
    @field:Pattern(
        regexp = "^[A-Za-z][A-Za-z0-9_]*$",
        message = "角色编码只能包含英文字母、数字和下划线，且必须以字母开头"
    )
    val code: String,

    @field:NotBlank(message = "角色名称不能为空")
    @field:Size(max = 50, message = "角色名称不能超过50个字符")
    val name: String,

    @field:Size(max = 255, message = "角色描述不能超过255个字符")
    val description: String? = null

)