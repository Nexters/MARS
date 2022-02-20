package com.ojicoin.cookiepang.config

import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.listener.RetryListenerSupport

/**
 * @author seongchan.kang
 */
class RetryListener : RetryListenerSupport() {

    override fun <T : Any?, E : Throwable?> onError(context: RetryContext?, callback: RetryCallback<T, E>?, throwable: Throwable?) {
        if (context?.retryCount == 1) return // onError 리스너이기 때문에 오류가 나고 재시도하기 전에 불림
        super.onError(context, callback, throwable)
    }

    override fun <T : Any?, E : Throwable?> close(context: RetryContext?, callback: RetryCallback<T, E>?, throwable: Throwable?) {
        // do nothing
    }
}
