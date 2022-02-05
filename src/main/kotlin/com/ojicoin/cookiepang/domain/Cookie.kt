package com.ojicoin.cookiepang.domain

import java.time.Instant
import org.springframework.context.ApplicationEvent
import org.springframework.data.annotation.Id
import org.springframework.data.domain.AbstractAggregateRoot
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
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
    @Column("status") val status: CookieStatus,
    @Column("tokenAddress") val tokenAddress: String,
    @Column("cookie_tag_id") val cookieTagId: Long,
) : AbstractAggregateRoot<Cookie>() {
    fun addEvent(event: ApplicationEvent) {
        registerEvent(event)
    }
}

enum class CookieStatus {
    HIDDEN,
    ACTIVE,
}
