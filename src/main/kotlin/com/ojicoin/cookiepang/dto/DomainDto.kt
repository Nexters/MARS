package com.ojicoin.cookiepang.dto

class DomainDto

data class CreateCookie(
    val question: String,
    val answer: String,
    val price: Long,
    val authorUserId: Long,
    val ownedUserId: Long,
    val tokenAddress: String,
    val categoryId: Long,
)
