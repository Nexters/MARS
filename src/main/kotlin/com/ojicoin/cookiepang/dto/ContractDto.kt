package com.ojicoin.cookiepang.dto

import java.math.BigInteger
import java.time.LocalDateTime

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
    val tokenId: BigInteger
)

enum class CookieEventStatus(private val num: Int) {
    CREATE(0), MODIFY(1), BUY(2);

    companion object {
        fun findByNum(num: Int): CookieEventStatus {
            return values().filter { it.num == num }.first()
        }
    }
}

data class CookieEvent(
    val cookieEventStatus: CookieEventStatus,
    val cookieId: BigInteger?,
    val fromAddress: String?,
    val hammerPrice: BigInteger?,
    val createdAt: LocalDateTime
)
