package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("user_tags")
class UserTag(
    @Id @Column("user_tag_id") var id: Long?,
    @Column("user_id") val userId: Long,
    @Column("tag_id") val tagId: Long,
)
