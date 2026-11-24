package com.eggetteluo.oasystem.auth.util

import com.eggetteluo.oasystem.auth.dto.JwtPayload
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtUtil {

    private val secretString = "mySuperSecretKeyForOaSystemJwtToken2026!"
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretString.toByteArray())
    private val expirationTime = Duration.ofHours(24)

    /**
     * 生成 JWT
     */
    fun generateToken(userId: UUID, jti: String): String {
        val now = System.currentTimeMillis()

        return Jwts.builder()
            .subject(userId.toString())
            .id(jti)
            .issuedAt(Date(now))
            .expiration(Date(now + expirationTime.toMillis()))
            .signWith(secretKey)
            .compact()
    }

    /**
     * 解析 JWT
     */
    fun parseToken(token: String): JwtPayload? {
        val claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload

        val userIdText = claims.subject ?: return null
        val userId = UUID.fromString(userIdText)
        val jti = claims.id ?: return null
        val expiration = claims.expiration ?: return null

        return JwtPayload(
            userId = userId,
            jti = jti,
            expiresAt = expiration.toInstant()
        )
    }

}