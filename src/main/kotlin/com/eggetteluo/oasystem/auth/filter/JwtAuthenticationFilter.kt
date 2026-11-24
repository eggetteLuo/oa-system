package com.eggetteluo.oasystem.auth.filter

import com.eggetteluo.oasystem.auth.util.JwtUtil
import com.eggetteluo.oasystem.user.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        // 1. 如果没有 Bearer Token，直接放行（比如登录请求，或者匿名访问）
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)
        // 2. 解析 Token，如果 Token 无效（过期或被篡改），extractUsername 会返回 null
        val username = jwtUtil.extractUsername(token)

        // 3. 如果 Token 有效，且当前上下文没有认证信息
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userEntity = userRepository.findByUsername(username)

            // 4. 如果用户存在，构建认证信息并存入上下文
            if (userEntity != null) {
                try {
                    val userDetails = User.withUsername(userEntity.username)
                        .password(userEntity.password)
                        .authorities("ROLE_USER")
                        .build()

                    val authToken = UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                    val context = SecurityContextHolder.createEmptyContext()
                    context.authentication = authToken
                    SecurityContextHolder.setContext(context)
                } catch (e: Exception) {
                    // 忽略构建用户详情时的偶发异常，不让请求死在这里
                    logger.warn("构建用户认证信息失败: ${e.message}")
                }
            }
        }

        // 5. 无论 Token 是否有效，最终都放行请求。
        // 如果没认证，后面的 SecurityConfig 发现没权限，自然会给前端返回 401 或 403
        filterChain.doFilter(request, response)
    }
}