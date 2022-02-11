package com.ojicoin.cookiepang.repository

import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.Category
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieStatus
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.UserCategory
import com.ojicoin.cookiepang.domain.ViewCount
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface CookieRepository : PagingAndSortingRepository<Cookie, Long> {
    fun findByTokenAddress(tokenAddress: String): Cookie?

    fun findByStatusIsNotAndCategoryId(
        status: CookieStatus = CookieStatus.DELETED,
        categoryId: Long,
        pageable: Pageable,
    ): List<Cookie>

    fun findByStatusIsNot(status: CookieStatus = CookieStatus.DELETED, pageable: Pageable): List<Cookie>
}

interface AskRepository : CrudRepository<Asks, Long>
interface CategoryRepository : CrudRepository<Category, Long>
interface UserRepository : CrudRepository<User, Long>
interface ViewCountRepository : CrudRepository<ViewCount, Long> {
    fun findAllByCookieId(cookieId: Long): List<ViewCount>
}

interface UserCategoryRepository : CrudRepository<UserCategory, Long> {
    fun findAllByUserId(userId: Long): List<UserCategory>
}
