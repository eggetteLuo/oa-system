package com.eggetteluo.oasystem.common.exception

import com.eggetteluo.oasystem.common.response.ApiResponse
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import tools.jackson.databind.DatabindException

/**
 * 全局异常处理器
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    // 处理业务抛出的异常
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ApiResponse<Unit> {
        return ApiResponse.error(e.code, e.message)
    }

    // 处理数据库唯一约束冲突（例如：插入重复的用户名或工号）
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityException(e: DataIntegrityViolationException): ApiResponse<Unit> {
        e.printStackTrace() // 将错误打印在控制台，方便查询
        return ApiResponse.error(400, "数据已存在或不符合规范，请检查唯一字段")
    }

    // 处理JSON异常
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(e: HttpMessageNotReadableException): ApiResponse<Unit> {
        val cause = e.cause
        if (cause is DatabindException) {
            val path = cause.path
            if (path.isNotEmpty()) {
                val fieldName = path[0].propertyName
                if (fieldName != null) {
                    return ApiResponse.error(400, "缺少必要的参数: '$fieldName'")
                }
            }
        }
        // 如果无法提取具体字段，返回通用提示
        return ApiResponse.error(400, "请求体格式错误或缺少必要的参数")
    }

    // 处理 @Valid 参数校验失败异常
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ApiResponse<Unit> {
        val errorMessage = e.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "参数校验失败"
        return ApiResponse.error(400, errorMessage)
    }

    // 兜底异常：处理所有其他未捕获的异常（比如空指针、代码Bug）
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ApiResponse<Unit> {
        e.printStackTrace() // 在控制台打印完整报错，方便排查
        return ApiResponse.error(500, "系统内部错误，请联系管理员")
    }

}