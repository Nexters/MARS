package com.ojicoin.dto

import com.ojicoin.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDTO(
    val id: Long,
    val nickname: String,
    val introduction: String,
    val profileUrl: String?,
    val walletAddress: String

) {
    companion object {
        fun fromEntity(user: User): UserProfileDTO {
            return user.run {
                return UserProfileDTO(id = id, nickname = nickname, introduction = introduction, profileUrl = profileUrl, walletAddress = walletAddress)
            }
        }
    }
}
