package com.ojicoin.cookiepang.dto

import com.ojicoin.cookiepang.domain.AskStatus
import com.ojicoin.cookiepang.domain.CookieStatus

data class CreateCookie(
    val question: String,
    val answer: String,
    val price: Long,
    val authorUserId: Long,
    val ownedUserId: Long,
    val tokenAddress: String,
    val categoryId: Long,
)

data class ViewCategory(
    val categoryId: Long,
    val name: String,
    val color: String,
)

data class UpdateCookie(
    val price: Long?,
    val status: CookieStatus?,
    val purchaserUserId: Long?,
)

data class CreateAsk(
    val title: String,
    val senderUserId: Long,
    val receiverUserId: Long,
)

data class UpdateAsk(
    val title: String?,
    val status: AskStatus?,
)
