package com.eggetteluo.oasystem.auth.handler

import com.eggetteluo.oasystem.common.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class SecurityExceptionHandler(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint, AccessDeniedHandler {

    // 未认证：401
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        writeResponse(response, 401, "登录状态已失效，请重新登录")
    }

    // 权限不足：403
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        writeResponse(response, 403, "没有权限访问该资源")
    }

    private fun writeResponse(
        response: HttpServletResponse,
        code: Int,
        message: String
    ) {
        if (response.isCommitted) return

        response.status = code
        response.characterEncoding = "UTF-8"
        response.contentType = "application/json;charset=UTF-8"

        objectMapper.writeValue(
            response.outputStream,
            ApiResponse.error<Unit>(code, message)
        )
    }
}