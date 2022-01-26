package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.Size

@Table("inqueries")
class Inquery(
    @Id @Column("inquiry_id") var id: Long?,
    @Column("title") @field:Size(max = 255) val title: String,
    @Column("user_id") val userId: Long,
)
