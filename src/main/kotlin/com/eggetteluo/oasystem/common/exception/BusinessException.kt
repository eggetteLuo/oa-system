package com.eggetteluo.oasystem.common.exception

/**
 * 自定义业务异常
  */
class BusinessException(
    val code: Int = 400,
    override val message: String
) : RuntimeException(message)