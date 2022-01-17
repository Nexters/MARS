package com.ojicoin.domain

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.InsertStatement
import javax.validation.constraints.Size

enum class UserStatus { ACTIVE }

object Users : Table() {
    val id = long("user_id").autoIncrement()
    val nickname = varchar("nickname", 100)
    val introduction = varchar("introduction", 255)
    val profileUrl = varchar("profile_url", 255).nullable()
    val walletAddress = varchar("wallet_address", 255)
    val status = enumeration("status", UserStatus::class)
}

@Serializable
data class User(
    val id: Long,
    val nickname: String,
    val introduction: String,
    val profileUrl: String?,
    val walletAddress: String,
    val status: UserStatus
)

fun ResultRow.toUser() = User(
    id = this[Users.id],
    nickname = this[Users.nickname],
    introduction = this[Users.introduction],
    profileUrl = this[Users.profileUrl],
    walletAddress = this[Users.walletAddress],
    status = this[Users.status],
)

@Serializable
data class CreateUser(
    @field:Size(max = 100) val nickname: String,
    @field:Size(max = 255) val introduction: String,
    @field:Size(max = 255) val profileUrl: String?,
    @field:Size(max = 255) val walletAddress: String,
) {
    fun apply(insertStatement: InsertStatement<Number>) {
        insertStatement[Users.nickname] = this.nickname
        insertStatement[Users.introduction] = this.introduction
        insertStatement[Users.profileUrl] = this.profileUrl
        insertStatement[Users.walletAddress] = this.walletAddress
        insertStatement[Users.status] = UserStatus.ACTIVE
    }
}
