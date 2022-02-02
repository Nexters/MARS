package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.Size

@Table("inquiries")
class Inquiry(
    @Id @Column("inquiry_id") var id: Long? = null,
    @Column("title") @field:Size(max = 255) val title: String,
    @Column("sender_user_id") val senderUserId: Long,
    @Column("receiver_user_id") val receiverUserId: Long,
)
