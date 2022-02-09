package com.ojicoin.cookiepang.dto

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
)
