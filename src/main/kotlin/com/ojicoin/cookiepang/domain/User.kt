package com.ojicoin.cookiepang.domain

import com.ojicoin.cookiepang.domain.CookieStatus.HIDDEN
import com.ojicoin.cookiepang.dto.UpdateUser
import com.ojicoin.cookiepang.event.ViewCookieEvent
import com.ojicoin.cookiepang.exception.ForbiddenRequestException
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant
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
    @Column("finish_onboard") var finishOnboard: Boolean,
    @Column("last_notification_checked_at") var lastNotificationCheckedAt: Instant?,
) {
    fun view(cookie: Cookie) {
        if (cookie.status == HIDDEN && this.id != cookie.ownedUserId) {
            throw ForbiddenRequestException("Given cookie is hidden.")
                .with("cookieId", cookie.id!!)
                .with("cookieStatus", cookie.status)
                .with("viewerId", id!!)
        }

        if (cookie.ownedUserId != this.id) {
            cookie.addEvent(ViewCookieEvent(this, this.id!!, cookie.id!!))
        }
    }

    fun apply(updateUser: UpdateUser) {
        updateUser.profilePictureUrl?.also { this.profileUrl = it }
        updateUser.backgroundPictureUrl?.also { this.backgroundUrl = it }
        updateUser.introduction?.also { this.introduction = it }
    }
}

enum class UserStatus { ACTIVE }
