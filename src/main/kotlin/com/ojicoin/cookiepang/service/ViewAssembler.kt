package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.contract.config.ContractProperties
import com.ojicoin.cookiepang.domain.Action
import com.ojicoin.cookiepang.domain.Category
import com.ojicoin.cookiepang.domain.CookieHistory
import com.ojicoin.cookiepang.dto.CategoryView
import com.ojicoin.cookiepang.dto.CookieHistoryView
import com.ojicoin.cookiepang.dto.CookieView
import com.ojicoin.cookiepang.dto.TimelineCookieView
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
    @Autowired val categoryService: CategoryService,
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
        val cookieHistories = cookieService.findCookieHistories(cookieId).map { it.toCookieHistoryView() }
        val category = categoryService.getById(cookie.categoryId)

        return CookieView(
            question = cookie.title,
            answer = answer,
            collectorId = owner.id!!,
            collectorName = owner.nickname,
            collectorProfileUrl = owner.profileUrl,
            creatorId = creator.id!!,
            creatorName = creator.nickname,
            creatorProfileUrl = creator.profileUrl,
            contractAddress = contractProperties.address,
            nftTokenId = cookie.nftTokenId,
            viewCount = viewCount,
            price = cookie.price,
            histories = cookieHistories,
            myCookie = myCookie,
            category = category.toCategoryView()
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
            val creator = userService.getById(cookie.authorUserId)
            val myCookie = viewer.id == creator.id
            val answer = cookie.open(viewerId)
            val viewCount = viewCountService.getAllViewCountsByCookieId(cookie.id!!)

            TimelineCookieView(
                cookieId = cookie.id!!,
                creatorId = creator.id!!,
                creatorProfileUrl = creator.profileUrl,
                creatorName = creator.nickname,
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

fun CookieHistory.toCookieHistoryView(): CookieHistoryView =
    when (this.action) {
        Action.CREATE -> CookieHistoryView(
            action = this.action,
            content = "${this.creatorName}님이 '${this.title.abbreviate()}'를 망치 ${this.hammerPrice}톤으로 만들었습니다.",
            createdAt = this.createdAt
        )
        Action.MODIFY -> CookieHistoryView(
            action = this.action,
            content = "${this.creatorName}님이 '${this.title.abbreviate()}'를 망치 ${this.hammerPrice}톤으로 수정했습니다.",
            createdAt = this.createdAt
        )
        Action.BUY -> CookieHistoryView(
            action = this.action,
            content = "${this.creatorName}님이 '${this.title.abbreviate()}'를 망치 ${this.hammerPrice}톤으로 깠습니다.",
            createdAt = this.createdAt
        )
    }

fun Category.toCategoryView() = CategoryView(
    id = this.id!!,
    name = this.name,
    color = this.color.name
)
