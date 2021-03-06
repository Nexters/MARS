package com.ojicoin.cookiepang.dto

import com.ojicoin.cookiepang.domain.Action
import com.ojicoin.cookiepang.domain.CookieStatus
import com.ojicoin.cookiepang.domain.UserStatus
import java.math.BigInteger
import java.time.Instant

data class CookieDetailView(
    val cookieId: Long,
    val question: String,
    val answer: String?,
    val cookieStatus: CookieStatus,
    val collectorId: Long,
    val collectorName: String,
    val collectorProfileUrl: String?,
    val creatorId: Long,
    val creatorName: String,
    val creatorProfileUrl: String?,
    val contractAddress: String,
    val nftTokenId: BigInteger,
    val viewCount: Long,
    val price: Long,
    val histories: List<CookieHistoryView>,
    val myCookie: Boolean,
    val category: CategoryView,
)

data class CookieHistoryView(
    val action: Action,
    val content: String,
    val createdAt: Instant,
)

data class PageableView<T>(
    val totalCount: Long,
    val totalPageIndex: Int,
    val nowPageIndex: Int,
    val isLastPage: Boolean,
    val contents: List<T>
)

data class TimelineCookieView(
    val cookieId: Long,
    val creatorId: Long,
    val creatorProfileUrl: String?,
    val creatorName: String,
    val question: String,
    val answer: String?,
    val contractAddress: String,
    val nftTokenId: BigInteger,
    val viewCount: Long,
    val cookieImageUrl: String?,
    val price: Long,
    val myCookie: Boolean,
    val category: CategoryView?,
    val createdAt: Instant,
)

data class CategoryView(
    val id: Long,
    val name: String,
    val color: String,
)

data class FinishOnboardView(
    val isFinish: Boolean,
)

enum class GetUserCookieTarget {
    OWNED,
    AUTHOR,
}

data class ProblemResponse(
    val title: String,
    val status: Int,
    val detail: String,
)

data class LoginRequest(val walletAddress: String)

data class LoginResponse(val userId: Long)

data class UserCookieView(
    val cookieId: Long,
    val nftTokenId: BigInteger,
    val cookieImageUrl: String?,
    val ownedUserId: Long,
    val category: CategoryView,
    val cookieStatus: CookieStatus
)

data class UserView(
    val id: Long,
    val walletAddress: String,
    val nickname: String,
    val introduction: String?,
    val profileUrl: String?,
    val backgroundUrl: String?,
    val status: UserStatus,
    val finishOnboard: Boolean,
)

data class NotificationNewCount(
    val newCount: Long
)
