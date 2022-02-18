package com.ojicoin.cookiepang.service

import com.klaytn.caver.abi.datatypes.Type
import com.klaytn.caver.abi.datatypes.generated.Uint256
import com.klaytn.caver.contract.Contract
import com.klaytn.caver.contract.SendOptions
import com.klaytn.caver.methods.request.CallObject
import com.klaytn.caver.methods.request.KlayLogFilter
import com.klaytn.caver.methods.response.KlayLogs
import com.klaytn.caver.methods.response.KlayLogs.LogResult
import com.ojicoin.cookiepang.dto.CookieEvent
import com.ojicoin.cookiepang.dto.CookieEventStatus
import com.ojicoin.cookiepang.dto.CookieInfo
import com.ojicoin.cookiepang.dto.TransferInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone

/**
 * @author seongchan.kang
 */
@Service
class CookieContractService(
    private val cookieContract: Contract,
    @Value("\${contract.admin-address}") val adminAddress: String,
) {

    private val TRANSACTION_HEX_PREFIX_DIGIT_LENGTH = 2
    private val TRANSACTION_ZERONUM_DIGIT_LENGTH = 24
    private val TRANSACTION_ADDRESS_DIGIT_LENGTH = 66

    // FIXME: Event Log의 인덱스 값들에 대한 관리를 어떻게하면 좋을지 고민 필요..
    private val COOKIE_TRANSFER_ADDRESS_INDEX = 2
    private val COOKIE_EVENT_COOKIE_ID_INDEX = 2

    // FIXME: 커스텀 익셉션 추가
    fun getTransferInfoByTxHash(txHash: String): TransferInfo {
        return try {
            val transferLogs = getLogsByEventName(CookieContractEvent.TRANSFER.eventName)
            val txHashLog = transferLogs.map { obj: LogResult<*> -> obj as KlayLogs.Log }.filter { log: KlayLogs.Log -> log.transactionHash == txHash }.filter(indexedLogDataNotZeroAddressPredicate(COOKIE_TRANSFER_ADDRESS_INDEX)).first()
            val fromAddress = txHashLog.topics[1]
            val toAddress = txHashLog.topics[2]
            val nftTokenIdHexStr = txHashLog.topics[3]
            val blockNumber = txHashLog.blockNumber

            TransferInfo(fixAddressDigits(fromAddress)!!, fixAddressDigits(toAddress)!!, getBigIntegerFromHexStr(nftTokenIdHexStr)!!, blockNumber)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
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

    fun createDefaultCookies(tokenInfos: List<CookieInfo>): List<BigInteger?> {
        return tokenInfos.map { cookieInfo: CookieInfo -> mintCookieByAdmin(cookieInfo!!) }.toList()
    }

    fun getCookieEventsByNftTokenId(nftTokenId: BigInteger): List<CookieEvent> {
        return getCookieEventsByNftTokenId(DefaultBlockParameterName.EARLIEST, nftTokenId)
    }

    fun getCookieEventsByNftTokenId(fromBlock: DefaultBlockParameter, nftTokenId: BigInteger): List<CookieEvent> {
        return try {
            val logs = getLogsByEventName(fromBlock, CookieContractEvent.COOKIE_EVENTED.eventName)
            val filteredLogs = logs
                .map { obj: LogResult<*> -> obj as KlayLogs.Log }
                .filter(indexedLogDataPredicateByBigInteger(COOKIE_EVENT_COOKIE_ID_INDEX, nftTokenId))
                .toList()

            getCookieEvents(filteredLogs)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    private fun mintCookieByAdmin(cookieInfo: CookieInfo): BigInteger? {
        return try {
            val sendOptions = SendOptions(adminAddress, DefaultGasProvider.GAS_LIMIT)
            val functionParams: List<Any> = listOf(cookieInfo.creatorAddress, cookieInfo.title, cookieInfo.content, cookieInfo.imageUrl, cookieInfo.tag, cookieInfo.hammerPrice)
            val receiptData = cookieContract.getMethod(CookieContractMethod.MINT_COOKIE_BY_OWNER.methodName).send(functionParams, sendOptions)

            val log = receiptData.logs[0]
            val ntfTokenId = log.topics[3]

            // 0x prefix 제거를 위해 substring
            getBigIntegerFromHexStr(ntfTokenId)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    private fun getCookieEvents(logs: List<KlayLogs.Log>): List<CookieEvent> {
        return logs
            .map { log: KlayLogs.Log ->
                val indexedDatas = log.topics
                val normalDatas: String = log.data.substring(TRANSACTION_HEX_PREFIX_DIGIT_LENGTH)
                val splitLength = normalDatas.length / 2
                val hammerPriceHexStr = normalDatas.substring(0, splitLength)
                val createdAtHexStr = normalDatas.substring(splitLength)
                val createdAtTimestamp = getBigIntegerFromHexStr(createdAtHexStr)!!.toLong() * 1000

                val hammerPrice = getBigIntegerFromHexStr(hammerPriceHexStr)
                val createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAtTimestamp), TimeZone.getDefault().toZoneId())
                val cookieStatus: CookieEventStatus = CookieEventStatus.findByNum(getBigIntegerFromHexStr(indexedDatas[1])!!.toInt())
                val nftTokenId = getBigIntegerFromHexStr(indexedDatas[2])
                val fromAddress = fixAddressDigits(indexedDatas[3])

                CookieEvent(cookieStatus, nftTokenId, fromAddress, hammerPrice, createdAt, log.blockNumber)
            }
            .toList()
    }

    private fun getLogsByEventName(fromBlock: DefaultBlockParameter, eventName: String): List<LogResult<*>> {
        val filter = KlayLogFilter(fromBlock, DefaultBlockParameterName.LATEST, cookieContract.contractAddress, null)
        val klayLogs = cookieContract.getPastEvent(eventName, filter)
        val logs = klayLogs.logs
        if (logs.isEmpty()) {
            throw RuntimeException()
        }
        return logs
    }

    private fun getLogsByEventName(eventName: String): List<LogResult<*>> {
        return getLogsByEventName(DefaultBlockParameterName.EARLIEST, eventName)
    }

    private fun getBigIntegerFromHexStr(nftTokenIdHexStr: String): BigInteger? {
        return BigInteger(nftTokenIdHexStr.substring(TRANSACTION_HEX_PREFIX_DIGIT_LENGTH), 16)
    }

    private fun fixAddressDigits(address: String): String? {
        require(address.length == TRANSACTION_ADDRESS_DIGIT_LENGTH)
        return address.substring(0, TRANSACTION_HEX_PREFIX_DIGIT_LENGTH) + address.substring(TRANSACTION_HEX_PREFIX_DIGIT_LENGTH + TRANSACTION_ZERONUM_DIGIT_LENGTH, TRANSACTION_ADDRESS_DIGIT_LENGTH)
    }

    private fun indexedLogDataNotZeroAddressPredicate(index: Int): (KlayLogs.Log) -> Boolean {
        return { log: KlayLogs.Log ->
            val fromAddress = log.topics[index]
            val result = getBigIntegerFromHexStr(fromAddress)
            result != BigInteger.ZERO
        }
    }

    private fun indexedLogDataPredicateByBigInteger(index: Int, expectedValue: BigInteger): (KlayLogs.Log) -> Boolean {
        return { log: KlayLogs.Log ->
            val nftTokenIdHexStr = log.topics[index]
            val value = getBigIntegerFromHexStr(nftTokenIdHexStr)
            expectedValue == value
        }
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
