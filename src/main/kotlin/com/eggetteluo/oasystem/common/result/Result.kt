package com.eggetteluo.oasystem.common.result

/**
 * 接口统一返回格式
 */
data class Result<T>(
    val code: Int,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T): Result<T> = Result(200, "操作成功", data)
        fun <T> success(message: String, data: T): Result<T> = Result(200, message, data)
        fun <T> error(code: Int, message: String): Result<T> = Result(code, message, null)
    }
}