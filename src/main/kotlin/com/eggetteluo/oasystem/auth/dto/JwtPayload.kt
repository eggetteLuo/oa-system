package com.eggetteluo.oasystem.auth.dto

import java.time.Instant
import java.util.UUID

data class JwtPayload(
    val userId: UUID,
    val jti: String,
    val expiresAt: Instant
)
