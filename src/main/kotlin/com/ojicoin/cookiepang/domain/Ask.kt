package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.Size

@Table("asks")
class Ask(
    @Id @Column("ask_id") var id: Long? = null,
    @Column("title") @field:Size(max = 255) val title: String,
    @Column("status") var status: AskStatus,
    @Column("sender_user_id") val senderUserId: Long,
    @Column("receiver_user_id") val receiverUserId: Long,
)

enum class AskStatus {
    PENDING,
    IGNORED,
    ACCEPTED,
}
