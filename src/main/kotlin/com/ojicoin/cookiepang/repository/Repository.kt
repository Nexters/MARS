package com.ojicoin.cookiepang.repository

import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieTag
import com.ojicoin.cookiepang.domain.Inquiry
import com.ojicoin.cookiepang.domain.Tag
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.ViewCount
import org.springframework.data.repository.CrudRepository

interface CookieRepository : CrudRepository<Cookie, Long>
interface CookieTagRepository : CrudRepository<CookieTag, Long>
interface InquiryRepository : CrudRepository<Inquiry, Long>
interface TagRepository : CrudRepository<Tag, Long>
interface UserRepository : CrudRepository<User, Long>
interface ViewCountRepository : CrudRepository<ViewCount, Long>
