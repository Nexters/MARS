package com.ojicoin.cookiepang.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import java.util.Collections

/**
 * @author user
 */
@Configuration
class RetryConfig {

    @Bean
    fun retryTemplate(): RetryTemplate {
        val retryTemplate = RetryTemplate()

        retryTemplate.registerListener(RetryListener()) // 리스너 등록

        val fixedBackOffPolicy = FixedBackOffPolicy() // 재시도 간격 설정
        fixedBackOffPolicy.backOffPeriod = 1000L
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy)

        val maxAttempts = 10 // 재시도 최대 횟수 설정
        val retryPolicy = SimpleRetryPolicy(maxAttempts, Collections.singletonMap(Exception::class.java, true))

        retryTemplate.setRetryPolicy(retryPolicy) // 위 설정을 적용

        return retryTemplate
    }
}
