package com.ojicoin.cookiepang.domain

import org.springframework.context.ApplicationEvent
import org.springframework.data.annotation.Id
import org.springframework.data.domain.AbstractAggregateRoot
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import javax.validation.constraints.Size

@Table("cookies")
data class Cookie(
    @Id @Column("cookie_id") var id: Long?,
    @Column("title") @field:Size(max = 255) var title: String,
    @Column("price") var price: Long,
    @Column("content") var content: String,
    @Column("image_url") @field:Size(max = 255) var imageUrl: String?,
    @Column("author_user_id") var authorUserId: Long,
    @Column("owned_user_id") var ownedUserId: Long,
    @Column("created_at") var createdAt: Instant,
    @Column("cookie_tag_id") var cookieTagId: Long,
) : AbstractAggregateRoot<Cookie>() {

    fun addEvent(event: ApplicationEvent) {
        registerEvent(event)
    }
}
