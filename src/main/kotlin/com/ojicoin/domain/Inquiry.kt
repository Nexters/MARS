package com.ojicoin.domain

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Inquiries : Table() {
    val id = long("inquiry_id").autoIncrement()
    val title = varchar("nickname", 100)
    val userId = long("user_id")
}

@Serializable
data class Inquiry(
    val title: String,
    val userId: Long,
)
