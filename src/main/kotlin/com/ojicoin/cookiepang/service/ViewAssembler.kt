package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.controller.Action
import com.ojicoin.cookiepang.controller.CookieHistory
import com.ojicoin.cookiepang.controller.CookieView
import com.ojicoin.cookiepang.domain.CookieStatus.HIDDEN
import java.time.Instant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ViewAssembler(
    @Autowired val cookieService: CookieService,
    @Autowired val userService: UserService,
    @Autowired val viewCountService: ViewCountService,
) {

    fun cookieView(cookieId: Long): CookieView {
        val cookie = cookieService.get(cookieId)
        val creator = userService.getById(cookie.authorUserId)
        val owner = userService.getById(cookie.ownedUserId)
        val viewCount = viewCountService.getAllViewCountsByCookieId(cookieId)
        val answer: String? = if (cookie.status == HIDDEN) {
            null
        } else {
            cookie.content
        }

        return CookieView(
            question = cookie.title,
            answer = answer,
            collectorName = owner.nickname,
            creatorName = creator.nickname,
            contractAddress = cookie.contractAddress,
            tokenAddress = cookie.tokenAddress,
            viewCount = viewCount,
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
}
