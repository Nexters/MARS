package com.ojicoin.cookiepang.domain

import com.ojicoin.cookiepang.dto.UpdateAsk
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.Size

@Table("asks")
class Ask(
    @Id @Column("ask_id") var id: Long? = null,
    @Column("title") @field:Size(max = 255) var title: String,
    @Column("status") var status: AskStatus,
    @Column("sender_user_id") val senderUserId: Long,
    @Column("receiver_user_id") val receiverUserId: Long,
) {
    fun apply(updateAsk: UpdateAsk) {
        updateAsk.title?.also { title = it }
        updateAsk.status?.also { status = it }
    }
}

enum class AskStatus {
    PENDING,
    IGNORED,
    ACCEPTED,
    DELETED,
}
