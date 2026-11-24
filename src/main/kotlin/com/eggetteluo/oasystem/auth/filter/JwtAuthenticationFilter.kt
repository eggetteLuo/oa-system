package com.eggetteluo.oasystem.auth.filter

import com.eggetteluo.oasystem.auth.service.TokenService
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
    private val userRepository: UserRepository,
    private val tokenService: TokenService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)
        val username = jwtUtil.extractUsername(token)

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            // 校验Token是否在Redis还存在
            if (!tokenService.isTokenValid(username)) {
                filterChain.doFilter(request, response)
                return
            }

            val userEntity = userRepository.findByUsername(username)
            if (userEntity != null) {
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
            }
        }

        filterChain.doFilter(request, response)
    }
}