package com.ojicoin.cookiepang.contract.service

import com.klaytn.caver.Caver
import com.klaytn.caver.methods.response.TransactionReceipt.TransactionReceiptData
import com.ojicoin.cookiepang.contract.dto.TransactionInfo
import com.ojicoin.cookiepang.exception.BlockNotFoundException
import com.ojicoin.cookiepang.exception.InvalidBlockChainRequestException
import com.ojicoin.cookiepang.service.RetryService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.web3j.protocol.core.Response
import java.math.BigInteger

/**
 * @author seongchan.kang
 */
@Service
class TransactionService(
    private val caver: Caver,
    private val retryService: RetryService,
    @Value("\${contract.addr.cookie}") private val cookieContractAddress: String,
) {
    private val TRANSACTION_HEX_PREFIX_DIGIT_LENGTH = 2

    fun getTransactionInfoByTxHash(txHash: String): TransactionInfo {
        return retryService.run(
            action = {
                val receipt: Response<TransactionReceiptData> = caver.rpc.klay.getTransactionReceipt(txHash).send()
                if (receipt.error != null) {
                    throw InvalidBlockChainRequestException(cookieContractAddress, "BlockChain Request is failed.")
                        .with("error", receipt.error)
                }
                if (receipt.result == null) {
                    throw BlockNotFoundException("Given txHash has no block")
                        .with("txHash", txHash)
                }
                TransactionInfo(
                    receipt.result.blockHash,
                    getBigIntegerFromHexStr(receipt.result.blockNumber)!!,
                    receipt.result.from, receipt.result.senderTxHash,
                    receipt.result.transactionHash
                )
            }, exceptions = listOf(BlockNotFoundException::class.java)
            )
        }

        private fun getBigIntegerFromHexStr(cookieIdHex: String): BigInteger? {
            return BigInteger(cookieIdHex.substring(TRANSACTION_HEX_PREFIX_DIGIT_LENGTH), 16)
        }
    }
    