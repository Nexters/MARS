package com.ojicoin.cookiepang.domain

import com.ojicoin.cookiepang.domain.CookieStatus.HIDDEN
import com.ojicoin.cookiepang.dto.UpdateUser
import com.ojicoin.cookiepang.event.ViewCookieEvent
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import javax.validation.constraints.Size

@Table("users")
class User(
    @Id @Column("user_id") var id: Long? = null,
    @Column("wallet_address") @field:Size(max = 255) val walletAddress: String,
    @Column("nickname") @field:Size(max = 100) var nickname: String,
    @Column("introduction") @field:Size(max = 255) var introduction: String?,
    @Column("profile_url") @field:Size(max = 255) var profileUrl: String?,
    @Column("background_url") @field:Size(max = 255) var backgroundUrl: String?,
    @Column("status") var status: UserStatus,
) {
    fun view(cookie: Cookie) {
        if (cookie.status == HIDDEN && this.id != cookie.ownedUserId) {
            throw IllegalArgumentException("cookie $cookie.id is hidden, only owner could view")
        }

        if (cookie.ownedUserId != this.id) {
            cookie.addEvent(ViewCookieEvent(this, this.id!!, cookie.id!!))
        }
    }

    fun apply(profileUrl: String?, backgroundUrl: String?, dto: UpdateUser) {
        profileUrl?.also { this.profileUrl = it }
        backgroundUrl?.also { this.backgroundUrl = it }
        dto.introduction?.also { this.introduction = it }
    }
}

enum class UserStatus { ACTIVE }
