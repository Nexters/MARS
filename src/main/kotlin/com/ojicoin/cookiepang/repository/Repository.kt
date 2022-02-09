package com.ojicoin.cookiepang.repository

import com.ojicoin.cookiepang.domain.Category
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieCategory
import com.ojicoin.cookiepang.domain.Inquiry
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.UserCategory
import com.ojicoin.cookiepang.domain.ViewCount
import org.springframework.data.repository.CrudRepository

interface CookieRepository : CrudRepository<Cookie, Long> {
    fun findByTokenAddress(tokenAddress: String): Cookie?
}

interface CookieTagRepository : CrudRepository<CookieCategory, Long>
interface InquiryRepository : CrudRepository<Inquiry, Long>
interface TagRepository : CrudRepository<Category, Long>
interface UserRepository : CrudRepository<User, Long>
interface ViewCountRepository : CrudRepository<ViewCount, Long> {
    fun findAllByCookieId(cookieId: Long): List<ViewCount>
}

interface UserCategoryRepository : CrudRepository<UserCategory, Long> {
    fun findAllByUserId(userId: Long): List<UserCategory>
}
