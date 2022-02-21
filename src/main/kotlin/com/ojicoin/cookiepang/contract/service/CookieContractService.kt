package com.ojicoin.cookiepang.contract.service

import com.klaytn.caver.abi.datatypes.Type
import com.klaytn.caver.abi.datatypes.generated.Uint256
import com.klaytn.caver.contract.Contract
import com.klaytn.caver.contract.SendOptions
import com.klaytn.caver.methods.request.CallObject
import com.klaytn.caver.methods.request.KlayLogFilter
import com.klaytn.caver.methods.response.KlayLogs
import com.klaytn.caver.methods.response.KlayLogs.LogResult
import com.ojicoin.cookiepang.contract.dto.CookieInfo
import com.ojicoin.cookiepang.contract.event.CookieEventLog
import com.ojicoin.cookiepang.contract.event.TransferEventLog
import com.ojicoin.cookiepang.contract.utils.parser.CookieEventLogParser
import com.ojicoin.cookiepang.contract.utils.parser.TransferEventLogParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.DefaultBlockParameterNumber
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger

/**
 * @author seongchan.kang
 */
@Service
class CookieContractService(
    private val cookieContract: Contract,
    private val cookieEventLogParser: CookieEventLogParser,
    private val transferEventLogParser: TransferEventLogParser,
    private val transactionService: TransactionService,
    @Value("\${contract.admin-address}") val adminAddress: String,
    @Value("\${contract.addr.cookie}") private val cookieContractAddress: String,
) {

    fun getCookieContractAddress(): String = cookieContractAddress

    fun getTransferEventLogByTxHash(txHash: String): TransferEventLog {
        val blockNumber: BigInteger = transactionService.getBlockNumberByTxHash(txHash)
        val transferLogs: List<LogResult<*>> = getLogsByEventName(
            DefaultBlockParameterNumber(blockNumber),
            DefaultBlockParameterNumber(blockNumber),
            CookieContractEvent.TRANSFER.eventName
        )

        val txHashLogs: List<KlayLogs.Log> = transferLogs.map { obj: LogResult<*> -> obj as KlayLogs.Log }
            .filter { log: KlayLogs.Log -> log.transactionHash == txHash }.toList()
        return transferEventLogParser.parse(txHashLogs).last()
    }

    fun getContent(nftTokenId: BigInteger, senderAddress: String): String {
        return try {
            val callObject = CallObject.createCallObject(senderAddress)
            val callResult = cookieContract.call(callObject, CookieContractMethod.GET_CONTENT.methodName, nftTokenId)
            val result: Type<String> = callResult[0] as Type<String>

            result.value
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    val mintingPriceForHammer: BigInteger
        get() = try {
            val callResult = cookieContract.call(CookieContractMethod.MINTING_PRICE_FOR_HAMMER.methodName)
            val result: Type<BigInteger> = callResult[0] as Type<BigInteger>

            result.value
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }

    val mintingPriceForKlaytn: BigInteger
        get() {
            return try {
                val callResult = cookieContract.call(CookieContractMethod.MINTING_PRICE_FOR_KALYTN.methodName)
                val result: Type<BigInteger> = callResult[0] as Type<BigInteger>

                result.value
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException()
            }
        }

    fun getOwnedNtfTokenIds(senderAddress: String): List<String> {
        return try {
            val callObject = CallObject.createCallObject(senderAddress)
            val callResult = cookieContract.call(callObject, CookieContractMethod.GET_OWNED_NFT_TOKEN_IDS.methodName)
            val result: Type<List<Uint256>> = callResult[0] as Type<List<Uint256>>

            result.value
                .map { uint256: Uint256 -> uint256.getValue().toString() }
                .toList()
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    fun balanceOf(address: String): BigInteger {
        return try {
            val callResult = cookieContract.call(CookieContractMethod.BALANCE_OF.methodName, address)
            val result: Type<BigInteger> = callResult[0] as Type<BigInteger>

            result.value
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    fun isHide(nftTokenId: BigInteger): Boolean {
        return try {
            val callResult = cookieContract.call(CookieContractMethod.IS_HIDE.methodName, nftTokenId)
            val result: Type<Boolean> = callResult[0] as Type<Boolean>

            result.value
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    fun isSale(nftTokenId: BigInteger): Boolean {
        return try {
            val callResult = cookieContract.call(CookieContractMethod.IS_SALE.methodName, nftTokenId)
            val result: Type<Boolean> = callResult[0] as Type<Boolean>

            result.value
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    fun getHammerPrice(nftTokenId: BigInteger): BigInteger {
        return try {
            val callResult = cookieContract.call(CookieContractMethod.GET_HAMMER_PRICE.methodName, nftTokenId)
            val result: Type<BigInteger> = callResult[0] as Type<BigInteger>

            result.value
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    fun getNtfTokenIdByIndex(senderAddress: String, index: BigInteger): BigInteger {
        return try {
            val callObject = CallObject.createCallObject(senderAddress)
            val callResult = cookieContract.call(callObject, CookieContractMethod.TOKEN_BY_INDEX.methodName, index)
            val result: Type<BigInteger> = callResult[0] as Type<BigInteger>

            result.value
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    val totalSupply: BigInteger
        get() {
            return try {
                val callResult = cookieContract.call(CookieContractMethod.TOTAL_SUPPLY.methodName)
                val result: Type<BigInteger> = callResult[0] as Type<BigInteger>

                result.value
            } catch (e: Exception) {
                e.printStackTrace()
                throw RuntimeException()
            }
        }

    fun createDefaultCookie(tokenInfo: CookieInfo): TransferEventLog? {
        return mintCookieByAdmin(tokenInfo)
    }

    fun getCookieEventLogByNftTokenId(nftTokenId: BigInteger): List<CookieEventLog> {
        return getCookieEventLogByNftTokenId(DefaultBlockParameterName.EARLIEST, nftTokenId)
    }

    fun getCookieEventLogByNftTokenId(fromBlock: DefaultBlockParameter, nftTokenId: BigInteger): List<CookieEventLog> {
        return try {
            val logs = getLogsByEventName(fromBlock, CookieContractEvent.COOKIE_EVENTED.eventName)
                .map { obj: LogResult<*> -> obj as KlayLogs.Log }
                .toList()
            cookieEventLogParser.parse(logs).filter { log: CookieEventLog -> log.nftTokenId == nftTokenId }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    private fun mintCookieByAdmin(cookieInfo: CookieInfo): TransferEventLog? {
        return try {
            val sendOptions = SendOptions(adminAddress, DefaultGasProvider.GAS_LIMIT)
            val functionParams: List<Any> = listOf(
                cookieInfo.creatorAddress,
                cookieInfo.title,
                cookieInfo.content,
                cookieInfo.imageUrl,
                cookieInfo.tag,
                cookieInfo.hammerPrice
            )
            val receiptData = cookieContract.getMethod(CookieContractMethod.MINT_COOKIE_BY_OWNER.methodName)
                .send(functionParams, sendOptions)

            transferEventLogParser.parse(receiptData.logs).first()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    private fun getLogsByEventName(fromBlock: DefaultBlockParameter, eventName: String): List<LogResult<*>> {
        return getLogsByEventName(fromBlock, DefaultBlockParameterName.LATEST, eventName)
    }

    private fun getLogsByEventName(
        fromBlock: DefaultBlockParameter,
        toBlock: DefaultBlockParameter,
        eventName: String,
    ): List<LogResult<*>> {
        val filter = KlayLogFilter(fromBlock, DefaultBlockParameterName.LATEST, cookieContract.contractAddress, null)
        val klayLogs = cookieContract.getPastEvent(eventName, filter)
        val logs = klayLogs.logs
        if (logs.isEmpty()) {
            throw RuntimeException()
        }
        return logs
    }
}

enum class CookieContractMethod(val methodName: String) {
    GET_CONTENT("getContent"),
    MINTING_PRICE_FOR_HAMMER("mintingPriceForHammer"),
    MINTING_PRICE_FOR_KALYTN("mintingPriceForKlaytn"),
    GET_OWNED_NFT_TOKEN_IDS("getOwnedCookieIds"),
    BALANCE_OF("balanceOf"),
    IS_HIDE("hideCookies"),
    IS_SALE("saleCookies"),
    GET_HAMMER_PRICE("cookieHammerPrices"),
    TOKEN_BY_INDEX("tokenByIndex"),
    TOTAL_SUPPLY("totalSupply"),
    MINT_COOKIE_BY_OWNER("mintCookieByOwner");
}

enum class CookieContractEvent(val eventName: String) {
    TRANSFER("Transfer"),
    COOKIE_EVENTED("CookieEvented");
}
