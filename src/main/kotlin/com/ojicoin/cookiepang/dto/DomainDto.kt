package com.ojicoin.cookiepang.dto

import com.ojicoin.cookiepang.domain.AskStatus
import com.ojicoin.cookiepang.domain.CookieStatus
import org.springframework.web.multipart.MultipartFile
import javax.validation.constraints.Size

data class CreateCookie(
    val question: String,
    val answer: String,
    val price: Long,
    val authorUserId: Long,
    val ownedUserId: Long,
    val txHash: String,
    val categoryId: Long,
)

data class UpdateCookie(
    val txHash: String?,
    val price: Long?,
    val status: CookieStatus?,
    val purchaserUserId: Long?,
)

data class CreateAsk(
    val title: String,
    val senderId: Long,
    val receiverId: Long,
    val categoryId: Long,
)

data class UpdateAsk(
    val title: String?,
    val status: AskStatus?,
    val categoryId: Long?,
)

data class CreateUser(
    val walletAddress: String,

    @field:Size(max = 100)
    val nickname: String,
    val introduction: String?,
    val profileUrl: String?,
    val backgroundUrl: String?,
    val deviceToken: String?,
)

data class UpdateUserRequest(
    val introduction: String?,
    val profilePicture: MultipartFile?,
    val backgroundPicture: MultipartFile?,
)

data class UpdateDeviceTokenRequest(
    val deviceToken: String?,
)

data class UpdateUser(
    val introduction: String?,
    val profilePictureUrl: String?,
    val backgroundPictureUrl: String?
)

data class CreateUserCategory(
    val categoryIdList: List<Long>,
)
