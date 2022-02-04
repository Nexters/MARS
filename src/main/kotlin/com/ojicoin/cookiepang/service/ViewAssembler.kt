package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.controller.Action.BUY
import com.ojicoin.cookiepang.controller.Action.CREATE
import com.ojicoin.cookiepang.controller.Action.MODIFY
import com.ojicoin.cookiepang.controller.CookieHistory
import com.ojicoin.cookiepang.controller.CookieView
import com.ojicoin.cookiepang.controller.Feed
import com.ojicoin.cookiepang.controller.TimelineView
import com.ojicoin.cookiepang.domain.CookieStatus.ACTIVE
import java.time.Instant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ViewAssembler(
    @Autowired val cookieService: CookieService,
    @Autowired val userService: UserService,
    @Autowired val viewCountService: ViewCountService,
    @Value("\${contract.address}") val contractAddress: String,
) {

    fun cookieView(viewUserId: Long, cookieId: Long): CookieView {
        val cookie = cookieService.get(cookieId)
        val creator = userService.getById(cookie.authorUserId)
        val owner = userService.getById(cookie.ownedUserId)
        val viewCount = viewCountService.getAllViewCountsByCookieId(cookieId)
        val answer: String? = if (viewUserId != owner.id) {
            null
        } else {
            cookie.content
        }

        // TODO: 블록체인 네트워크에서 히스토리 조회후 변환 로직 추가
        return CookieView(
            question = cookie.title,
            answer = answer,
            collectorName = owner.nickname,
            creatorName = creator.nickname,
            contractAddress = contractAddress,
            tokenAddress = cookie.tokenAddress,
            viewCount = viewCount,
            price = cookie.price,
            histories = listOf(
                CookieHistory(
                    action = CREATE,
                    content = """
                            '상일동 치타'님이 'Q.내가 여자친구가 있을까'를 망치 34개로 만들었습니다.
                    """.trimIndent(),
                    createdAt = Instant.now(),
                ),
                CookieHistory(
                    action = BUY,
                    content = """
                            '강동구 호랑이'님이 'Q.내가 여자친구가 있을까'를 망치 34개로 깠습니다.
                    """.trimIndent(),
                    createdAt = Instant.now(),
                ),
                CookieHistory(
                    action = MODIFY,
                    content = """
                            '강동구 호랑이'님이 'Q.내가 여자친구가 있을까'를 망치 32개로 수정했습니다.
                    """.trimIndent(),
                    createdAt = Instant.now()
                )
            )
        )
    }

    fun timelineView(userId: Long): TimelineView {
        val cookies = cookieService.findTimelineCookies()

        val feeds = cookies.map {
            val nickname = userService.getById(it.ownedUserId).nickname
            Feed(
                userNickname = nickname,
                question = it.title,
                answer = if (it.status == ACTIVE) {
                    it.content
                } else {
                    null
                },
                price = it.price,
                viewCount = 0L,
                createdAt = Instant.now()
            )
        }

        return TimelineView(feeds)
    }
}
