package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import javax.validation.constraints.Size

@Table("cookies")
data class Cookie(
    @Id @Column("cookie_id") var id: Long?,
    @Column("title") @field:Size(max = 255) val title: String,
    @Column("price") val price: Long,
    @Column("content") val content: String,
    @Column("image_url") @field:Size(max = 255) val imageUrl: String?,
    @Column("author_user_id") val authorUserId: Long,
    @Column("owned_user_id") val ownedUserId: Long,
    @Column("created_at") val createdAt: Instant,
    @Column("cookie_tag_id") val cookieTagId: Long,
)
