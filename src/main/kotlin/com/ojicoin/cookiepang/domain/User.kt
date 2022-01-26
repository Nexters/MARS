package com.ojicoin.cookiepang.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.Size

@Table("users")
class User(
    @Id @Column("user_id") var id: Long?,
    @Column("nickname") @field:Size(max = 100) val nickname: String,
    @Column("introduction") @field:Size(max = 255) val introduction: String,
    @Column("profile_url") @field:Size(max = 255) val profileUrl: String,
    @Column("wallet_address") @field:Size(max = 255) val walletAddress: String,
    @Column("status") val status: UserStatus,
)

enum class UserStatus { ACTIVE }
