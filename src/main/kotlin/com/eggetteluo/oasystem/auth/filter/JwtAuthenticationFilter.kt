package com.eggetteluo.oasystem.auth.filter

import com.eggetteluo.oasystem.auth.service.TokenService
import com.eggetteluo.oasystem.user.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userRepository: UserRepository,
    private val tokenService: TokenService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 已经认证过则不重复处理
        if (SecurityContextHolder.getContext().authentication != null) {
            filterChain.doFilter(request, response)
            return
        }

        // 获取请求头
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        // 获取 Token
        val token = authHeader.substring(7).trim()
        if (token.isEmpty()) {
            filterChain.doFilter(request, response)
            return
        }

        // 校验 JWT 和 Redis 中的 JTI
        val userId = tokenService.getUserIdByToken(token)

        if (userId != null) {
            // 查询用户信息
            val userEntity = userRepository.findByIdOrNull(userId)
            if (userEntity != null) {
                val userDetails = User.withUsername(userEntity.username)
                    .password("")
                    .authorities("ROLE_USER")
                    .build()

                // 创建认证对象
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )

                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                // 设置 Spring Security 上下文
                val context = SecurityContextHolder.createEmptyContext()
                context.authentication = authentication

                SecurityContextHolder.setContext(context)
            } else {
                // 用户已不存在，清除对应登录状态
                tokenService.deleteToken(token)
            }
        }

        // 继续执行过滤器链
        filterChain.doFilter(request, response)
    }
}
