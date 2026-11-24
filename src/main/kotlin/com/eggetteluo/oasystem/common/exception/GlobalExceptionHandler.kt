package com.eggetteluo.oasystem.common.exception

import com.eggetteluo.oasystem.common.result.Result
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 全局异常处理器
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    // 处理业务抛出的异常
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): Result<Unit> {
        return Result.Companion.error(e.code, e.message)
    }

    // 处理数据库唯一约束冲突（例如：插入重复的用户名或工号）
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityException(e: DataIntegrityViolationException): Result<Unit> {
        // 为了安全，不把底层 SQL 报错直接透给前端
        return Result.Companion.error(400, "数据已存在或不符合规范，请检查唯一字段")
    }

    // 兜底异常：处理所有其他未捕获的异常（比如空指针、代码Bug）
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): Result<Unit> {
        e.printStackTrace() // 在控制台打印完整报错，方便排查
        return Result.Companion.error(500, "系统内部错误，请联系管理员")
    }

}