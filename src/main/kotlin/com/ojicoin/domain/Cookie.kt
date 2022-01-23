package com.ojicoin.domain

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object Cookies : Table() { // TOOD: tokenId가 필요한지 확인
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
    val id: Long,
    val title: String,
    val price: Long,
    val content: String,
    val imageUrl: String?,
    val authUserId: Long,
    val createdAt: kotlinx.datetime.Instant,
    val cookieTagId: Long,
)

fun ResultRow.toCookie() = Cookie(
    id = this[Cookies.id],
    title = this[Cookies.title],
    price = this[Cookies.price],
    content = this[Cookies.content],
    imageUrl = this[Cookies.imageUrl],
    authUserId = this[Cookies.authorUserId],
    createdAt = this[Cookies.createdAt],
    cookieTagId = this[Cookies.cookieTagId]
)
