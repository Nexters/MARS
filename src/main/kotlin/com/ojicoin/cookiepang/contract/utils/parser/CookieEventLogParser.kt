package com.ojicoin.cookiepang.contract.utils.parser

import com.klaytn.caver.Caver
import com.klaytn.caver.abi.EventValues
import com.klaytn.caver.abi.datatypes.Type
import com.klaytn.caver.contract.ContractIOType
import com.klaytn.caver.methods.response.KlayLogs
import com.ojicoin.cookiepang.contract.event.CookieEventLog
import com.ojicoin.cookiepang.contract.event.CookieEventStatus
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDateTime
import java.util.Arrays
import java.util.TimeZone

/**
 * @author seongchan.kang
 */
@Service
class CookieEventLogParser(private val caver: Caver) : LogParser<CookieEventLog> {
    override fun parse(logs: List<KlayLogs.Log>): List<CookieEventLog> {
        return logs
            .map { log: KlayLogs.Log ->
                parseCookieEventLog(log)
            }
            .toList()
    }

    override fun parse(log: KlayLogs.Log): CookieEventLog {
        return parseCookieEventLog(log)
    }

    private fun parseCookieEventLog(log: KlayLogs.Log): CookieEventLog {
        val topics = log.topics
        val nonIndexedData = log.data
        val cookieEventedLog = Arrays.asList(
            ContractIOType("eventStatus", "uint8", true),
            ContractIOType("cookieId", "uint256", true),
            ContractIOType("from", "address", true),
            ContractIOType("hammerPrice", "uint256", false),
            ContractIOType("createdAt", "uint256", false)
        )
        var eventValues: EventValues = caver.abi.decodeLog(cookieEventedLog, nonIndexedData, topics)

        val eventStatusValue: Type<BigInteger> = eventValues!!.indexedValues[0] as Type<BigInteger>
        val cookieIdValue: Type<BigInteger> = eventValues.indexedValues[1] as Type<BigInteger>
        val fromAddressValue: Type<String> = eventValues.indexedValues[2] as Type<String>
        val hammerPriceValue: Type<BigInteger> = eventValues.nonIndexedValues[0] as Type<BigInteger>
        val createdAtValue: Type<BigInteger> = eventValues.nonIndexedValues[1] as Type<BigInteger>
        val createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAtValue.value.toLong() * 1000), TimeZone.getDefault().toZoneId())

        return CookieEventLog(CookieEventStatus.findByNum(eventStatusValue.value.toInt()), cookieIdValue.value, fromAddressValue.value, hammerPriceValue.value, createdAt, log.blockNumber)
    }
}
