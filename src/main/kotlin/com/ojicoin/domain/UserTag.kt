package com.ojicoin.domain

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.InsertStatement

object UserTags : Table() {
    val userId = long("user_id")
    val tagId = long("tag_id")
}

@Serializable
data class UserTag(
    val userId: Long,
    val tagId: Long,
)

fun ResultRow.toUserTag() = UserTag(
    userId = this[UserTags.userId],
    tagId = this[UserTags.tagId]
)

@Serializable
data class CreateUserTag(
    val userId: Long,
    val tagId: Long,
) {
    fun apply(insertSerializable: InsertStatement<Number>) {
        insertSerializable[UserTags.userId] = this.userId
        insertSerializable[UserTags.tagId] = this.tagId
    }
}
