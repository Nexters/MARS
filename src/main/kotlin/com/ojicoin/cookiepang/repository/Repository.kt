package com.ojicoin.cookiepang.repository

import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.AskStatus
import com.ojicoin.cookiepang.domain.AskStatus.PENDING
import com.ojicoin.cookiepang.domain.Category
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieHistory
import com.ojicoin.cookiepang.domain.CookieStatus
import com.ojicoin.cookiepang.domain.CookieStatus.ACTIVE
import com.ojicoin.cookiepang.domain.CookieStatus.DELETED
import com.ojicoin.cookiepang.domain.Notification
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.UserCategory
import com.ojicoin.cookiepang.domain.ViewCount
import org.springframework.data.domain.Pageable
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import java.time.Instant

interface CookieRepository : PagingAndSortingRepository<Cookie, Long> {
    fun findByStatusIsAndCategoryId(
        status: CookieStatus = ACTIVE,
        categoryId: Long,
        pageable: Pageable,
    ): List<Cookie>

    fun countByStatusIsAndCategoryId(
        status: CookieStatus = ACTIVE,
        categoryId: Long,
    ): Long

    @Query("""SELECT * FROM "cookies" c WHERE c."status" != 'DELETED' AND c."cookie_id" = :cookieId""")
    fun findCookieById(cookieId: Long): Cookie?

    fun findByStatusIs(status: CookieStatus = ACTIVE, pageable: Pageable): List<Cookie>

    fun countByStatusIs(status: CookieStatus = ACTIVE): Long

    fun findByStatusIsNotAndOwnedUserId(
        status: CookieStatus = DELETED,
        ownedUserId: Long,
        pageable: Pageable,
    ): List<Cookie>

    fun countByStatusIsNotAndOwnedUserId(
        status: CookieStatus = DELETED,
        ownedUserId: Long,
    ): Long

    fun findByStatusIsNotAndAuthorUserId(
        status: CookieStatus = DELETED,
        authorUserId: Long,
        pageable: Pageable,
    ): List<Cookie>

    fun countByStatusIsNotAndAuthorUserId(
        status: CookieStatus = DELETED,
        authorUserId: Long,
    ): Long
}

interface AskRepository : PagingAndSortingRepository<Ask, Long> {
    fun findBySenderId(senderId: Long, pageable: Pageable): List<Ask>

    fun countBySenderId(senderId: Long): Long

    fun findByReceiverIdAndStatus(receiverId: Long, status: AskStatus = PENDING, pageable: Pageable): List<Ask>

    fun countByReceiverIdAndStatus(receiverId: Long, status: AskStatus = PENDING): Long
}

interface CategoryRepository : CrudRepository<Category, Long> {
    fun findByName(name: String): Category?
}

interface UserRepository : CrudRepository<User, Long> {
    fun findByNickname(nickname: String): User?
    fun findByWalletAddress(walletAddress: String): User?
}

interface ViewCountRepository : CrudRepository<ViewCount, Long> {
    fun findAllByCookieId(cookieId: Long): List<ViewCount>
}

interface UserCategoryRepository : CrudRepository<UserCategory, Long> {
    fun findAllByUserId(userId: Long): List<UserCategory>
}

interface NotificationRepository : PagingAndSortingRepository<Notification, Long> {
    fun findAllByReceiverUserId(receiverUserId: Long, pageable: Pageable): List<Notification>

    fun countAllByCreatedAtAfter(lastCheckedAt: Instant): Long
}

interface CookieHistoryRepository : CrudRepository<CookieHistory, Long> {
    fun findByCookieId(cookieId: Long): List<CookieHistory>
}
