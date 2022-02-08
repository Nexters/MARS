package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("cookie_categories")
class CookieCategory(
    @Id @Column("cookie_category_id") var id: Long?,
    @Column("cookie_id") val cookieId: Long,
    @Column("category_id") val categoryId: Long,
)
