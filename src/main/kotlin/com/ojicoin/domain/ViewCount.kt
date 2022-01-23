package com.ojicoin.domain

import kotlinx.datetime.Clock.System
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.statements.InsertStatement

object ViewCounts : Table() {
    val id = long("view_count_id").autoIncrement()
    val userId = long("user_id")
    val cookieId = long("cookie_id")
    val count = long("count")
    val createdAt = timestamp("created_at")
}

data class ViewCount(
    val id: Long,
    val userId: Long,
    val cookieId: Long,
    val count: Long,
)

fun ResultRow.toViewCount() = ViewCount(
    id = this[ViewCounts.id],
    userId = this[ViewCounts.userId],
    cookieId = this[ViewCounts.cookieId],
    count = this[ViewCounts.count],
)

@Serializable
data class CreateViewCount(
    val userId: Long,
    val cookieId: Long,
    val count: Long,
) {
    fun apply(insertSerializable: InsertStatement<Number>) {
        insertSerializable[ViewCounts.userId] = this.userId
        insertSerializable[ViewCounts.cookieId] = this.cookieId
        insertSerializable[ViewCounts.count] = this.count
        insertSerializable[ViewCounts.createdAt] = System.now()
    }
}
