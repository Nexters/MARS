package com.ojicoin.cookiepang.dto

import java.math.BigInteger

/*
* TokenInfo.java 2022. 02. 16
*/
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

data class TransferInfo(
    val fromAddrees: String,
    val toAddress: String,
    val cookieId: BigInteger
)
