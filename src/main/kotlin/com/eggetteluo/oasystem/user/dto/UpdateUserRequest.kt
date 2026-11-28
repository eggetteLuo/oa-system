package com.eggetteluo.oasystem.user.dto

import com.eggetteluo.oasystem.user.enums.UserStatus
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.util.UUID

/**
 * 更新用户请求
 *
 * 所有字段传 null 表示不修改；
 * email / phone 传空字符串表示清空。
 */
data class UpdateUserRequest(

    @field:NotBlank(message = "姓名不能为空")
    @field:Size(max = 50, message = "姓名不能超过50个字符")
    val realName: String,

    @field:Email(message = "邮箱格式不正确")
    @field:Size(max = 100, message = "邮箱不能超过100个字符")
    val email: String? = null,

    @field:Size(max = 20, message = "手机号不能超过20个字符")
    val phone: String? = null,

    val departmentId: UUID? = null,

    val roleId: UUID? = null,

    val status: UserStatus? = null

)
