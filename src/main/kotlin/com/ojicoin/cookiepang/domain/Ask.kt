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
    @Column("category_id") var categoryId: Long,
    @Column("sender_id") val senderId: Long,
    @Column("receiver_id") val receiverId: Long,
) {
    fun apply(updateAsk: UpdateAsk) {
        updateAsk.title?.also { title = it }
        updateAsk.status?.also { status = it }
        updateAsk.categoryId?.also { categoryId = it }
    }
}

enum class AskStatus {
    PENDING,
    IGNORED,
    ACCEPTED,
    DELETED,
}
