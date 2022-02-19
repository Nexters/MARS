package com.ojicoin.cookiepang.contract.service

import com.klaytn.caver.Caver
import com.klaytn.caver.methods.response.TransactionReceipt.TransactionReceiptData
import org.springframework.stereotype.Service
import org.web3j.protocol.core.Response
import java.io.IOException
import java.math.BigInteger

/**
 * @author seongchan.kang
 */
@Service
class TransactionService(
    private val caver: Caver
) {
    private val TRANSACTION_HEX_PREFIX_DIGIT_LENGTH = 2

    fun getBlockNumberByTxHash(txHash: String): BigInteger {
        return try {
            val receipt: Response<TransactionReceiptData> = caver.rpc.klay.getTransactionReceipt(txHash).send()
            val blockNumber: String = receipt.result.blockNumber!!
            getBigIntegerFromHexStr(blockNumber)!!
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    private fun getBigIntegerFromHexStr(cookieIdHex: String): BigInteger? {
        return BigInteger(cookieIdHex.substring(TRANSACTION_HEX_PREFIX_DIGIT_LENGTH), 16)
    }
}
