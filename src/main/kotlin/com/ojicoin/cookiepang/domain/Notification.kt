package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
import javax.validation.constraints.Size

@Table("notifications")
class Notification(
    @Id @Column("notification_id") var id: Long? = null,
    @Column("type") val type: NotificationType,
    @Column("title") @field:Size(max = 50) val title: String,
    @Column("content") @field:Size(max = 300) val content: String,
    @Column("receiver_user_id") val receiverUserId: Long,
    @Column("sender_user_id") val senderUserId: Long? = null,
    @Column("created_at") val createdAt: Instant,
    @Column("checked") var checked: Boolean = false,

    // for ask type
    @Column("ask_id") val askId: Long? = null,
    // for cookie type
    @Column("cookie_id") val cookieId: Long? = null,
)

enum class NotificationType(
    val title: String,
    val pushMessageTitle: String,
) {
    Ask("요청", "질문을 요청받았어요."),
    Transaction("판매", "내 쿠키가 판매되었어요."),
}
