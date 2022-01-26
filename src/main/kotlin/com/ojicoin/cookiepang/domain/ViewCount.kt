package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("view_counts")
class ViewCount(
    @Id @Column("view_count_id") var id: Long?,
    @Column("user_id") val userId: Long,
    @Column("cookie_id") val cookieId: Long,
    @Column("count") val count: Long,
    @Column("created_at") val createdAt: Instant,
)
