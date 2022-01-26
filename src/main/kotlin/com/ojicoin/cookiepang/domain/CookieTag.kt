package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("cookie_tags")
class CookieTag(
    @Id @Column("cookie_tag_id") var id: Long?,
    @Column("cookie_id") val cookieId: Long,
    @Column("tag_id") val tagId: Long,
)
