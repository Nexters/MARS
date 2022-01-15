package com.ojicoin.domain

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Cookies : Table() {
    val id = long("cookie_id").autoIncrement()
    val title = varchar("title", 255)
    val price = long("price")
    val content = text("content")
    val imageUrl = varchar("image_url", 255).nullable()
    val authorUserId = long("author_user_id")
    val createdAt = timestamp("created_at")
    val cookieTagId = long("cookie_tag_id")
}

@Serializable
data class Cookie(
    val title: String,
    val price: Long,
    val content: String,
    val imageUrl: String?,
    val createdAt: kotlinx.datetime.Instant
)

data class NewCookie(
    val title: String,
    val price: Long,
    val content: String,
    val imageUrl: String? = null,
)
