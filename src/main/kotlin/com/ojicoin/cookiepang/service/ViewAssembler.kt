package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.contract.config.ContractProperties
import com.ojicoin.cookiepang.domain.Action
import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.Category
import com.ojicoin.cookiepang.domain.CookieHistory
import com.ojicoin.cookiepang.dto.CategoryView
import com.ojicoin.cookiepang.dto.CookieDetailView
import com.ojicoin.cookiepang.dto.CookieHistoryView
import com.ojicoin.cookiepang.dto.PageableView
import com.ojicoin.cookiepang.dto.TimelineCookieView
import com.ojicoin.cookiepang.dto.UserCookieView
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import kotlin.math.ceil

const val ABBREVIATE_LENGTH_LIMIT = 15

@Component
class ViewAssembler(
    @Autowired val cookieService: CookieService,
    @Autowired val userService: UserService,
    @Autowired val askService: AskService,
    @Autowired val viewCountService: ViewCountService,
    @Autowired val categoryService: CategoryService,
    @Autowired val contractProperties: ContractProperties,
) {

    fun cookieView(viewerId: Long, cookieId: Long): CookieDetailView {
        val cookie = cookieService.get(cookieId)
        val creator = userService.getById(cookie.authorUserId)
        val owner = userService.getById(cookie.ownedUserId)
        val viewer = userService.getById(viewerId)
        val myCookie = viewerId == owner.id
        val answer: String? = cookie.open(viewerId)

        viewer.view(cookie)
        cookieService.publishEvent(cookie)

        val viewCount = viewCountService.getAllViewCountsByCookieId(cookieId)
        val cookieHistories =
            cookieService.findCookieHistories(cookieId).map { it.toCookieHistoryView() }
                .sortedByDescending { it.createdAt }
        val category = categoryService.getById(cookie.categoryId)

        return CookieDetailView(
            cookieId = cookieId,
            question = cookie.title,
            answer = answer,
            cookieStatus = cookie.status,
            collectorId = owner.id!!,
            collectorName = owner.nickname,
            collectorProfileUrl = owner.profileUrl,
            creatorId = creator.id!!,
            creatorName = creator.nickname,
            creatorProfileUrl = creator.profileUrl,
            contractAddress = contractProperties.cookie,
            nftTokenId = cookie.nftTokenId,
            viewCount = viewCount,
            price = cookie.price,
            histories = cookieHistories,
            myCookie = myCookie,
            category = category.toCategoryView()
        )
    }

    fun timelineView(
        viewerId: Long,
        viewCategoryId: Long? = null,
        page: Int = 0,
        size: Int = 3
    ): PageableView<TimelineCookieView> {
        val viewer = userService.getById(viewerId)
        val pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        val allCookieSize = if (viewCategoryId != null) {
            cookieService.countActiveCookiesByCategoryId(categoryId = viewCategoryId)
        } else {
            cookieService.countActiveCookies()
        }
        val cookies = if (viewCategoryId != null) {
            cookieService.getActiveCookiesByCategoryId(categoryId = viewCategoryId, pageable = pageable)
        } else {
            cookieService.getActiveCookies(pageable = pageable)
        }

        val totalPageSize = getTotalPageSize(allCookieSize, size)
        val cookieViews = cookies.map { cookie ->
            val creator = userService.getById(cookie.authorUserId)
            val myCookie = viewer.id == cookie.ownedUserId
            val answer = cookie.open(viewerId)
            val viewCount = viewCountService.getAllViewCountsByCookieId(cookie.id!!)
            val category = categoryService.getById(cookie.categoryId)

            TimelineCookieView(
                cookieId = cookie.id!!,
                creatorId = creator.id!!,
                creatorProfileUrl = creator.profileUrl,
                creatorName = creator.nickname,
                question = cookie.title,
                answer = answer,
                contractAddress = contractProperties.cookie,
                nftTokenId = cookie.nftTokenId,
                viewCount = viewCount,
                cookieImageUrl = cookie.imageUrl,
                category = category.toCategoryView(),
                myCookie = myCookie,
                price = cookie.price,
                createdAt = cookie.createdAt,
            )
        }

        return PageableView(
            totalCount = allCookieSize,
            totalPageIndex = if (totalPageSize == 0) {
                0
            } else {
                totalPageSize - 1
            },
            nowPageIndex = page,
            isLastPage = lastPage(totalPageSize = totalPageSize, pageIndex = page),
            contents = cookieViews
        )
    }

    fun ownedCookiesView(userId: Long, page: Int = 0, size: Int = 3): PageableView<UserCookieView> {
        val cookies = cookieService.getAllOwnedCookies(
            ownedUserId = userId,
            pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        )

        val totalCookiesCount = cookieService.countAllOwnedCookies(ownedUserId = userId)

        val userCookieViews = cookies.map {
            val category = categoryService.getById(it.categoryId)
            UserCookieView(
                cookieId = it.id!!,
                nftTokenId = it.nftTokenId,
                cookieImageUrl = it.imageUrl,
                ownedUserId = it.ownedUserId,
                category = category.toCategoryView(),
                cookieStatus = it.status
            )
        }

        val totalPageSize = getTotalPageSize(totalSize = totalCookiesCount, size = size)
        return PageableView(
            totalCount = totalCookiesCount,
            totalPageIndex = if (totalPageSize == 0) {
                0
            } else {
                totalPageSize - 1
            },
            nowPageIndex = page,
            isLastPage = lastPage(totalPageSize = totalPageSize, pageIndex = page),
            contents = userCookieViews
        )
    }

    fun authorCookiesView(userId: Long, page: Int = 0, size: Int = 3): PageableView<UserCookieView> {
        val cookies = cookieService.getAllAuthorCookies(
            authorUserId = userId,
            pageable = PageRequest.of(page, size, Sort.by("createdAt").descending())
        )

        val totalCookiesCount = cookieService.countAllAuthorCookies(authorUserId = userId)

        val userCookieViews = cookies.map {
            val category = categoryService.getById(it.categoryId)
            UserCookieView(
                cookieId = it.id!!,
                nftTokenId = it.nftTokenId,
                cookieImageUrl = it.imageUrl,
                ownedUserId = it.ownedUserId,
                category = category.toCategoryView(),
                cookieStatus = it.status
            )
        }

        val totalPageSize = getTotalPageSize(totalSize = totalCookiesCount, size = size)
        return PageableView(
            totalCount = totalCookiesCount,
            totalPageIndex = if (totalPageSize == 0) {
                0
            } else {
                totalPageSize - 1
            },
            nowPageIndex = page,
            isLastPage = lastPage(totalPageSize = totalPageSize, pageIndex = page),
            contents = userCookieViews
        )
    }

    fun askViewAboutSender(
        userId: Long,
        page: Int = 0,
        size: Int = 3
    ): PageableView<Ask> {
        val allCountAsks = askService.countAsksBySenderId(senderId = userId)
        val totalPageSize = getTotalPageSize(allCountAsks, size)

        return PageableView(
            totalCount = allCountAsks,
            totalPageIndex = if (totalPageSize == 0) {
                0
            } else {
                totalPageSize - 1
            },
            nowPageIndex = page,
            isLastPage = lastPage(totalPageSize = totalPageSize, pageIndex = page),
            contents = askService.getBySenderId(userId, PageRequest.of(page, size))
        )
    }

    fun askViewAboutReceiver(
        userId: Long,
        page: Int = 0,
        size: Int = 3
    ): PageableView<Ask> {
        val allCountAsks = askService.countAsksByReceiverId(receiverId = userId)
        val totalPageSize = getTotalPageSize(allCountAsks, size)

        return PageableView(
            totalCount = allCountAsks,
            totalPageIndex = if (totalPageSize == 0) {
                0
            } else {
                totalPageSize - 1
            },
            nowPageIndex = page,
            isLastPage = lastPage(totalPageSize = totalPageSize, pageIndex = page),
            contents = askService.getByReceiverId(userId, PageRequest.of(page, size))
        )
    }

    // ??????(?????? ??? ?????? / ?????????) => ?????? ????????? ??????
    // ?????? ????????? ?????? == ????????? ????????? + 1 ??? ?????? ????????? ???????????? ????????????.
    private fun lastPage(totalPageSize: Int, pageIndex: Int) =
        totalPageSize <= pageIndex + 1

    private fun getTotalPageSize(totalSize: Long, size: Int) = ceil(totalSize.div(size.toDouble())).toInt()
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
            content = "${this.creatorName}?????? '${this.title.abbreviate()}'??? ?????? ${this.hammerPrice}????????? ??????????????????.",
            createdAt = this.createdAt
        )
        Action.MODIFY -> CookieHistoryView(
            action = this.action,
            content = "${this.creatorName}?????? '${this.title.abbreviate()}'??? ?????? ${this.hammerPrice}????????? ??????????????????.",
            createdAt = this.createdAt
        )
        Action.BUY -> CookieHistoryView(
            action = this.action,
            content = "${this.creatorName}?????? '${this.title.abbreviate()}'??? ?????? ${this.hammerPrice}????????? ????????????.",
            createdAt = this.createdAt
        )
    }

fun Category.toCategoryView() = CategoryView(
    id = this.id!!,
    name = this.name,
    color = this.color.name
)
