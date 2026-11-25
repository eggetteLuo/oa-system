package com.eggetteluo.oasystem.role.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateRoleRequest(

    @field:NotBlank(message = "角色名称不能为空")
    @field:Size(max = 50, message = "角色名称不能超过50个字符")
    val name: String,

    @field:Size(max = 255, message = "角色描述不能超过255个字符")
    val description: String? = null,

    val enabled: Boolean = true

)
