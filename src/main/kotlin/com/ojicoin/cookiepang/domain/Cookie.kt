package com.ojicoin.cookiepang.domain

import com.ojicoin.cookiepang.dto.UpdateCookie
import org.springframework.context.ApplicationEvent
import org.springframework.data.annotation.Id
import org.springframework.data.domain.AbstractAggregateRoot
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigInteger
import java.time.Instant
import javax.validation.constraints.Size

@Table("cookies")
data class Cookie(
    @Id @Column("cookie_id") var id: Long? = null,
    @Column("title") @field:Size(max = 255) val title: String,
    @Column("price") var price: Long,
    @Column("content") private val content: String,
    @Column("image_url") @field:Size(max = 255) val imageUrl: String?,
    @Column("author_user_id") val authorUserId: Long,
    @Column("owned_user_id") var ownedUserId: Long,
    @Column("created_at") val createdAt: Instant,
    @Column("status") var status: CookieStatus,
    @Column("tx_hash") val txHash: String,
    @Column("nft_token_id") val nftTokenId: BigInteger,
    @Column("from_block_address") var fromBlockAddress: BigInteger,
    @Column("categoryId") val categoryId: Long,
) : AbstractAggregateRoot<Cookie>() {

    fun addEvent(event: ApplicationEvent) {
        registerEvent(event)
    }

    fun open(userId: Long): String? = if (ownedUserId == userId) {
        content
    } else {
        null
    }

    fun apply(updateCookie: UpdateCookie) {
        updateCookie.price?.also { price = it }
        updateCookie.status?.also { status = it }
        updateCookie.purchaserUserId?.also { ownedUserId = it }
    }
}

enum class CookieStatus {
    HIDDEN,
    ACTIVE,
    DELETED,
}
