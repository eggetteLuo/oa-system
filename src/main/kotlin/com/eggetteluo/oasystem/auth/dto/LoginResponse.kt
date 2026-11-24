package com.eggetteluo.oasystem.auth.dto

data class LoginResponse(
    val token: String,
    val username: String,
    val realName: String
)
