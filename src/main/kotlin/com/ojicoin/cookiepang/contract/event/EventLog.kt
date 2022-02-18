package com.ojicoin.cookiepang.contract.event

import java.math.BigInteger
import java.time.LocalDateTime

/**
 * @author seongchan.kang
 */

abstract class Event(
    open val blockNumber: BigInteger
)

data class TransferEventLog(
    val fromAddrees: String,
    val toAddress: String,
    val nftTokenId: BigInteger,
    override val blockNumber: BigInteger
) : Event(blockNumber)

data class CookieEventLog(
    val cookieEventStatus: CookieEventStatus,
    val nftTokenId: BigInteger?,
    val fromAddress: String?,
    val hammerPrice: BigInteger?,
    val createdAt: LocalDateTime,
    override val blockNumber: BigInteger
) : Event(blockNumber)

enum class CookieEventStatus(private val num: Int) {
    CREATE(0), MODIFY(1), BUY(2);

    companion object {
        fun findByNum(num: Int): CookieEventStatus {
            return values().filter { it.num == num }.first()
        }
    }
}
