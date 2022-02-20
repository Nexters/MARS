package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigInteger
import java.time.Instant

@Table("cookie_histories")
data class CookieHistory(
    @Id @Column("cookie_history_id") var id: Long? = null,
    @Column("action") val action: Action,
    @Column("cookie_id") val cookieId: Long,
    @Column("creator_id") val creatorId: Long,
    @Column("hammer_price") val hammerPrice: BigInteger?,
    @Column("nft_token_id") val nftTokenId: BigInteger,
    @Column("block_number") val blockNumber: BigInteger,
    @Column("created_at") val createdAt: Instant,
)

enum class Action {
    MODIFY,
    BUY,
    CREATE,
}
