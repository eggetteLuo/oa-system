package com.eggetteluo.oasystem.auth.dto

data class TokenResponse(
    val token: String,
    val tokenType: String = "Bearer",
)
