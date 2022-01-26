package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.Size

@Table("tags")
class Tag(
    @Id @Column("tag_id") var id: Long?,
    @Column("name") @field:Size(max = 20) val name: String,
    @Column("cookie_tag_id") val cookieTagId: Long
)
