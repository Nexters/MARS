package com.ojicoin.cookiepang.repository

import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.AskStatus
import com.ojicoin.cookiepang.domain.Category
import com.ojicoin.cookiepang.domain.Cookie
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
import java.util.Optional

interface CookieRepository : PagingAndSortingRepository<Cookie, Long> {
    fun findByTxHash(txHash: String): Cookie?

    fun findByStatusIsNotAndCategoryId(
        status: CookieStatus = DELETED,
        categoryId: Long,
        pageable: Pageable,
    ): List<Cookie>

    @Query("""SELECT * FROM "cookies" c WHERE c."status" != 'DELETED' AND c."cookie_id" = :cookieId""")
    fun findActiveCookieById(cookieId: Long): Cookie?

    fun findByStatusIsNot(status: CookieStatus = DELETED, pageable: Pageable): List<Cookie>

    fun findByStatusIsNotAndOwnedUserId(
        status: CookieStatus = DELETED,
        ownedUserId: Long,
        pageable: Pageable
    ): List<Cookie>

    fun findByStatusAndOwnedUserId(status: CookieStatus = ACTIVE, ownedUserId: Long, pageable: Pageable): List<Cookie>

    fun findByStatusIsNotAndAuthorUserId(
        status: CookieStatus = DELETED,
        authorUserId: Long,
        pageable: Pageable
    ): List<Cookie>

    fun findByStatusAndAuthorUserId(status: CookieStatus = ACTIVE, authorUserId: Long, pageable: Pageable): List<Cookie>
}

interface AskRepository : CrudRepository<Ask, Long> {
    fun findBySenderUserId(senderUserId: Long): List<Ask>

    fun findByReceiverUserIdAndStatus(receiverUserId: Long, status: AskStatus): List<Ask>
}

interface CategoryRepository : CrudRepository<Category, Long>
interface UserRepository : CrudRepository<User, Long> {
    fun findByNickname(nickname: String): Optional<User>
    fun findByWalletAddress(walletAddress: String): Optional<User>
}

interface ViewCountRepository : CrudRepository<ViewCount, Long> {
    fun findAllByCookieId(cookieId: Long): List<ViewCount>
}

interface UserCategoryRepository : CrudRepository<UserCategory, Long> {
    fun findAllByUserId(userId: Long): List<UserCategory>
}

interface NotificationRepository : PagingAndSortingRepository<Notification, Long> {
    fun findAllByReceiverUserId(receiverUserId: Long, pageable: Pageable): List<Notification>
}
