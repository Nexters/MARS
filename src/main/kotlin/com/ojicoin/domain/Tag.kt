package com.ojicoin.domain

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Tags : Table() {
    val id = long("tag_id").autoIncrement()
    val name = varchar("name", 20)
    val cookieTagId = long("cookie_tag_id")
}

@Serializable
data class Tag(
    val id: Long,
    val name: String,
    val cookieTagId: Long,
)

fun ResultRow.toTag() = Tag(
    id = this[Tags.id],
    name = this[Tags.name],
    cookieTagId = this[Tags.cookieTagId]
)
