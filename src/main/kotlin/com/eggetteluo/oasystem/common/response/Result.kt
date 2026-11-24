package com.eggetteluo.oasystem.common.response

/**
 * 接口统一返回格式
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(200, "操作成功", data)
        fun <T> success(message: String, data: T): ApiResponse<T> = ApiResponse(200, message, data)
        fun <T> error(code: Int, message: String): ApiResponse<T> = ApiResponse(code, message, null)
    }
}