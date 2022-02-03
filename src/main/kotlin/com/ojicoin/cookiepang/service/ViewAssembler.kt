package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.controller.Action
import com.ojicoin.cookiepang.controller.CookieHistory
import com.ojicoin.cookiepang.controller.CookieView
import java.time.Instant
import org.springframework.stereotype.Component

@Component
class ViewAssembler {
    fun cookieView() = CookieView(
        question = "나에게 가장 수치스러운 것은?",
        answer = null,
        collectorName = "강동구 호랑이",
        creatorName = "강동구 호랑이",
        contractAddress = "0xed5a..c544",
        tokenAddress = "2342",
        viewCount = 25,
        histories = listOf(
            CookieHistory(
                action = Action.CREATE,
                content = """
                            '상일동 치타'님이 'Q.내가 여자친구가 있을까'를 망치 34개로 만들었습니다.
                          """.trimIndent(),
                createdAt = Instant.now(),
            ),
            CookieHistory(
                action = Action.BUY,
                content = """
                            '강동구 호랑이'님이 'Q.내가 여자친구가 있을까'를 망치 34개로 깠습니다.
                          """.trimIndent(),
                createdAt = Instant.now(),
            ),
            CookieHistory(
                action = Action.MODIFY,
                content = """
                            '강동구 호랑이'님이 'Q.내가 여자친구가 있을까'를 망치 32개로 수정했습니다.
                          """.trimIndent(),
                createdAt = Instant.now()
            )
        )
    )
}
