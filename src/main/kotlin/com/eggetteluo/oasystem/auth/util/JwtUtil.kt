package com.eggetteluo.oasystem.auth.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtil {

    private val secretString = "mySuperSecretKeyForOaSystemJwtToken2026!"
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(secretString.toByteArray())
    private val expirationTime = 86400000 // 24小时 (毫秒)

    // 生成 Token
    fun generateToken(username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expirationTime))
            .signWith(secretKey)
            .compact()
    }

    // 从 Token 中解析出用户名
    fun extractUsername(token: String): String? {
        return try {
            Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).payload.subject
        } catch (e: Exception) {
            null // 解析失败（过期或被篡改）返回 null
        }
    }

}