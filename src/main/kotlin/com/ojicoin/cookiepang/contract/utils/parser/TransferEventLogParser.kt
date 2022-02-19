package com.ojicoin.cookiepang.contract.utils.parser

import com.klaytn.caver.Caver
import com.klaytn.caver.abi.EventValues
import com.klaytn.caver.abi.datatypes.Type
import com.klaytn.caver.contract.ContractIOType
import com.klaytn.caver.methods.response.KlayLogs
import com.ojicoin.cookiepang.contract.event.TransferEventLog
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.util.Arrays

/**
 * @author seongchan.kang
 */
@Service
class TransferEventLogParser(private val caver: Caver) : LogParser<TransferEventLog> {
    override fun parse(logs: List<KlayLogs.Log>): List<TransferEventLog> {
        return logs
            .map { log: KlayLogs.Log -> parseTransferEventLog(log) }
            .toList()
    }

    override fun parse(log: KlayLogs.Log): TransferEventLog {
        return parseTransferEventLog(log)
    }

    private fun parseTransferEventLog(log: KlayLogs.Log): TransferEventLog {
        val topics = log.topics
        val nonIndexedData = log.data
        val cookieEventedLog = Arrays.asList(
            ContractIOType("from", "address", true),
            ContractIOType("to", "address", true),
            ContractIOType("tokenId", "uint256", true),
        )
        var eventValues: EventValues = caver.abi.decodeLog(cookieEventedLog, nonIndexedData, topics)

        val fromAddress: Type<String> = eventValues!!.indexedValues[0] as Type<String>
        val toAddress: Type<String> = eventValues.indexedValues[1] as Type<String>
        val nftTokenId: Type<BigInteger> = eventValues.indexedValues[2] as Type<BigInteger>

        return TransferEventLog(fromAddress.value, toAddress.value, nftTokenId.value, log.blockNumber)
    }
}
