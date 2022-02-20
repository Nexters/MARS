package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.contract.config.ContractProperties
import com.ojicoin.cookiepang.controller.CookieHistoryView
import com.ojicoin.cookiepang.controller.CookieView
import com.ojicoin.cookiepang.controller.TimelineCookieView
import com.ojicoin.cookiepang.domain.Action
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieHistory
import com.ojicoin.cookiepang.domain.User
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime

const val ABBREVIATE_LENGTH_LIMIT = 15

@Component
class ViewAssembler(
    @Autowired val cookieService: CookieService,
    @Autowired val userService: UserService,
    @Autowired val viewCountService: ViewCountService,
    @Autowired val contractProperties: ContractProperties,
) {

    fun cookieView(viewerId: Long, cookieId: Long): CookieView {
        val cookie = cookieService.get(cookieId)
        val creator = userService.getById(cookie.authorUserId)
        val owner = userService.getById(cookie.ownedUserId)
        val viewer = userService.getById(viewerId)
        val myCookie = viewerId == owner.id
        val answer: String? = cookie.open(viewerId)

        viewer.view(cookie)
        cookieService.publishEvent(cookie)

        val viewCount = viewCountService.getAllViewCountsByCookieId(cookieId)

        val cookieHistories = cookieService.findCookieHistories(cookieId).map { toCookieHistory(it, owner, cookie) }

        return CookieView(
            question = cookie.title,
            answer = answer,
            collectorName = owner.nickname,
            collectorProfileUrl = owner.profileUrl,
            creatorName = creator.nickname,
            creatorProfileUrl = creator.profileUrl,
            contractAddress = contractProperties.address,
            nftTokenId = cookie.nftTokenId,
            viewCount = viewCount,
            price = cookie.price,
            histories = cookieHistories,
            myCookie = myCookie
        )
    }

    fun timelineView(viewerId: Long, categoryId: Long? = null, page: Int = 0, size: Int = 3): List<TimelineCookieView> {
        val viewer = userService.getById(viewerId)
        val cookies = if (categoryId != null) {
            cookieService.getCookiesByCategoryId(categoryId = categoryId, page = page, size = size)
        } else {
            cookieService.getCookies(page = page, size = size)
        }

        return cookies.map { cookie ->
            val owner = userService.getById(cookie.ownedUserId)
            val myCookie = viewer.id == owner.id
            val answer = cookie.open(viewerId)
            val viewCount = viewCountService.getAllViewCountsByCookieId(cookie.id!!)

            TimelineCookieView(
                cookieId = cookie.id!!,
                collectorProfileUrl = owner.profileUrl,
                collectorName = owner.nickname,
                question = cookie.title,
                answer = answer,
                contractAddress = contractProperties.address,
                nftTokenId = cookie.nftTokenId,
                viewCount = viewCount,
                cookieImageUrl = cookie.imageUrl,
                myCookie = myCookie,
                price = cookie.price,
                createdAt = cookie.createdAt
            )
        }
    }

    private fun toCookieHistory(
        cookieHistory: CookieHistory,
        owner: User,
        cookie: Cookie,
    ): CookieHistoryView =
        when (cookieHistory.action) {
            Action.CREATE -> CookieHistoryView(
                action = cookieHistory.action,
                content = "${owner.nickname}님이 '${cookie.title.abbreviate()}'를 망치 ${cookie.price}톤으로 만들었습니다.",
                createdAt = cookieHistory.createdAt
            )
            Action.MODIFY -> CookieHistoryView(
                action = cookieHistory.action,
                content = "${owner.nickname}님이 '${cookie.title.abbreviate()}'를 망치 ${cookieHistory.hammerPrice}톤으로 수정했습니다.",
                createdAt = cookieHistory.createdAt
            )
            Action.BUY -> CookieHistoryView(
                action = cookieHistory.action,
                content = "${owner.nickname}님이 '${cookie.title.abbreviate()}'를 망치 ${cookie.price}톤으로 깠습니다.",
                createdAt = cookieHistory.createdAt
            )
        }
}

fun String.abbreviate(
    abbrevMarker: String = "..",
    maxWidth: Int = ABBREVIATE_LENGTH_LIMIT,
): String = StringUtils.abbreviate(
    this,
    abbrevMarker,
    maxWidth
)

fun LocalDateTime.toInstant(): Instant = this.toInstant(OffsetDateTime.now().offset)
