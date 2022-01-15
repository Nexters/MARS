package com.ojicoin.domain

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object CookieTags : Table() {
    val id = long("cookie_tag_id").autoIncrement()
    val cookieId = long("cookie_id")
    val tagId = long("tag_id")
}

@Serializable
data class CookieTag(
    val id: Long,
    val cookieId: Long,
    val tagId: Long,
)
