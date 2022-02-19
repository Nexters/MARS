package com.ojicoin.cookiepang.contract.dto

import java.math.BigInteger

/**
 * @author seongchan.kang
 */
data class CookieInfo(
    val creatorAddress: String,
    val title: String,
    val content: String,
    val imageUrl: String,
    val tag: String,
    val hammerPrice: BigInteger
)
