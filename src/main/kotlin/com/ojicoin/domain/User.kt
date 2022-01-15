package com.ojicoin.domain

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Users: Table() {
    val id = long("user_id").autoIncrement()
    val nickname = varchar("nickname", 100)
    val introduction = varchar("introduction", 255)
    val profileUrl = varchar("profile_url", 255)
    val walletAddress = varchar("wallet_address", 255)
    val status = varchar("status", 10)
}


@Serializable
data class User(
    val nickname: String,
    val introduction: String,
    val profileUrl: String,
    val walletAddress: String,
    val status: String
)
