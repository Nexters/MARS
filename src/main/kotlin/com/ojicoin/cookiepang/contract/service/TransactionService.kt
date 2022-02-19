package com.ojicoin.cookiepang.contract.service

import com.klaytn.caver.Caver
import com.klaytn.caver.methods.response.TransactionReceipt.TransactionReceiptData
import com.ojicoin.cookiepang.service.RetryService
import org.springframework.stereotype.Service
import org.web3j.protocol.core.Response
import java.math.BigInteger

/**
 * @author seongchan.kang
 */
@Service
class TransactionService(
    private val caver: Caver,
    private val retryService: RetryService
) {
    private val TRANSACTION_HEX_PREFIX_DIGIT_LENGTH = 2

    fun getBlockNumberByTxHash(txHash: String): BigInteger {
        return retryService.run(action = {
            val receipt: Response<TransactionReceiptData> = caver.rpc.klay.getTransactionReceipt(txHash).send()
            val blockNumber: String = receipt.result.blockNumber!!
            getBigIntegerFromHexStr(blockNumber)!!
        })
    }

    private fun getBigIntegerFromHexStr(cookieIdHex: String): BigInteger? {
        return BigInteger(cookieIdHex.substring(TRANSACTION_HEX_PREFIX_DIGIT_LENGTH), 16)
    }
}
