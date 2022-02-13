package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import javax.validation.constraints.Size

@Table("notifications")
class Notification(
    @Id @Column("notification_id") var id: Long? = null,
    @Column("type") @field:Size(max = 20) val type: NotificationType,
    @Column("content") @field:Size(max = 255) val content: String,
    @Column("receiver_user_id") val receiverUserId: Long,
    @Column("sender_user_id") val senderUserId: Long? = null,
    @Column("created_at") val createdAt: Instant,
)

enum class NotificationType { Ask, Transaction }
