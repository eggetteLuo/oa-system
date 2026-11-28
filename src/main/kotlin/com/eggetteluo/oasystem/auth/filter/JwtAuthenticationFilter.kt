package com.eggetteluo.oasystem.auth.filter

import com.eggetteluo.oasystem.auth.service.TokenService
import com.eggetteluo.oasystem.permission.repository.PermissionRepository
import com.eggetteluo.oasystem.permission.repository.RolePermissionRepository
import com.eggetteluo.oasystem.role.repository.RoleRepository
import com.eggetteluo.oasystem.user.enums.UserStatus
import com.eggetteluo.oasystem.user.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val rolePermissionRepository: RolePermissionRepository,
    private val permissionRepository: PermissionRepository,
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
        if (userId == null) {
            filterChain.doFilter(request, response)
            return
        }

        // 查询用户信息
        val userEntity = userRepository.findByIdOrNull(userId)

        // 用户不存在或已被软删除，清除登录状态
        if (userEntity == null || userEntity.deletedAt != null) {
            tokenService.deleteToken(token)
            filterChain.doFilter(request, response)
            return
        }

        // 检查用户状态，用户被禁用或离职也清除登录状态
        if (userEntity.status != UserStatus.ACTIVE) {
            tokenService.deleteToken(token)
            filterChain.doFilter(request, response)
            return
        }

        // 查询用户对应角色
        val roleEntity = roleRepository.findByIdOrNull(userEntity.roleId)

        // 角色不存在、被禁用、已删除不建立认证
        if (roleEntity == null || roleEntity.deletedAt != null || !roleEntity.enabled) {
            filterChain.doFilter(request, response)
            return
        }

        // 查询用户权限列表
        val rolePermissions = rolePermissionRepository.findAllByRoleId(userEntity.roleId)
            .filter { it.deletedAt == null }

        // 提取权限 ID
        val permissionIds = rolePermissions.map { it.permissionId }
            .toSet()

        // 根据权限 ID 批量查询详情
        val permissions = permissionRepository.findAllById(permissionIds)
            .filter {
                it.enabled && it.deletedAt == null
            }

        // 构建 Spring Security 权限合集
        val authorities = mutableSetOf<SimpleGrantedAuthority>()

        // 添加角色权限
        authorities.add(SimpleGrantedAuthority("ROLE_${roleEntity.code}"))

        // 添加业务权限
        permissions.forEach { permission ->
            authorities.add(SimpleGrantedAuthority(permission.code))
        }

        // 创建 UserDetails
        val userDetails = User.withUsername(userEntity.username)
            .password("")
            .authorities(authorities)
            .build()

        // 创建认证对象
        val authentication = UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            authorities
        )

        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

        // 设置 Spring Security 上下文
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authentication

        SecurityContextHolder.setContext(context)

        // 继续执行过滤器链
        filterChain.doFilter(request, response)
    }
}
