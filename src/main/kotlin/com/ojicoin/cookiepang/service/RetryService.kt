package com.ojicoin.cookiepang.service

import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service

/**
 * @author seongchan.kang
 */
@Service
class RetryService(
    private val retryTemplate: RetryTemplate
) {

    @Throws(Exception::class)
    fun <T> run(
        action: () -> T,
        maxAttempts: Int = 10,
        exceptions: List<Class<out Throwable>> = listOf(Exception::class.java),
        maxInterval: Long = 10000L
    ): T {

        val exceptionMap = exceptions.map { it to true }.toMap() // 재시도를 원하는 exception 등록
        val retryPolicy = SimpleRetryPolicy(maxAttempts, exceptionMap)
        retryTemplate.setRetryPolicy(retryPolicy)

        return retryTemplate.execute<T, Throwable> { // 재시도 수행
            action()
        }
    }
}
