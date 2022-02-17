package com.ojicoin.cookiepang.service

import com.klaytn.caver.abi.datatypes.Type
import com.klaytn.caver.abi.datatypes.generated.Uint256
import com.klaytn.caver.contract.Contract
import com.klaytn.caver.contract.SendOptions
import com.klaytn.caver.methods.request.CallObject
import com.klaytn.caver.methods.request.KlayLogFilter
import com.klaytn.caver.methods.response.KlayLogs
import com.ojicoin.cookiepang.dto.CookieInfo
import com.ojicoin.cookiepang.dto.TransferInfo
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.tx.gas.DefaultGasProvider
import java.io.IOException
import java.math.BigInteger
import java.util.function.Predicate
import java.util.stream.Collectors

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

    enum class CookieContractMethod(val methodName: String) {
        GET_CONTENT("getContent"),
        MINTING_PRICE_FOR_HAMMER("mintingPriceForHammer"),
        MINTING_PRICE_FOR_KALYTN("mintingPriceForKlaytn"),
        GET_OWNED_COOKIE_IDS("getOwnedCookieIds"),
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

    // FIXME: 커스텀 익셉션 추가
    fun getTransferInfoByTxHash(txHash: String): TransferInfo {
        return try {
            val transferLogs = getTransferLogs()
            val txHashLog = transferLogs.stream().map { obj: KlayLogs.LogResult<*> -> obj as KlayLogs.Log }.filter { log: KlayLogs.Log -> log.transactionHash == txHash }.filter(fromAddressNotZeroPredicate()).findFirst().orElseThrow { RuntimeException() }

            val fromAddress = txHashLog.topics[1]
            val toAddress = txHashLog.topics[2]
            val cookieIdHex = txHashLog.topics[3]

            TransferInfo(fixAddressDigits(fromAddress)!!, fixAddressDigits(toAddress)!!, getBigIntegerFromHexStr(cookieIdHex)!!)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    fun getContent(cookieId: String, senderAddress: String): String {
        return try {
            val callObject = CallObject.createCallObject(senderAddress)
            val callResult = cookieContract.call(callObject, CookieContractMethod.GET_CONTENT.methodName, cookieId)
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

    fun getOwnedCookieIds(senderAddress: String): List<String> {
        return try {
            val callObject = CallObject.createCallObject(senderAddress)
            val callResult = cookieContract.call(callObject, CookieContractMethod.GET_OWNED_COOKIE_IDS.methodName)
            val result: Type<List<Uint256>> = callResult[0] as Type<List<Uint256>>
            result.value.stream()
                .map { uint256: Uint256 -> uint256.getValue().toString() }
                .collect(Collectors.toList())
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

    fun isHide(cookieId: String): Boolean {
        return try {
            val callResult = cookieContract.call(CookieContractMethod.IS_HIDE.methodName, cookieId)
            val result: Type<Boolean> = callResult[0] as Type<Boolean>
            result.value
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    fun isSale(cookieId: String): Boolean {
        return try {
            val callResult = cookieContract.call(CookieContractMethod.IS_SALE.methodName, cookieId)
            val result: Type<Boolean> = callResult[0] as Type<Boolean>
            result.value
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    fun getHammerPrice(cookieId: String): BigInteger {
        return try {
            val callResult = cookieContract.call(CookieContractMethod.GET_HAMMER_PRICE.methodName, cookieId)
            val result: Type<BigInteger> = callResult[0] as Type<BigInteger>
            result.value
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    fun getCookieIdByIndex(senderAddress: String, index: BigInteger): BigInteger {
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
        return tokenInfos.stream().map { cookieInfo: CookieInfo -> mintCookieByAdmin(cookieInfo!!) }.collect(Collectors.toList())
    }

    /**
     *
     * @param cookieInfo
     * @return transaction hash 값
     */
    private fun mintCookieByAdmin(cookieInfo: CookieInfo): BigInteger? {
        return try {
            val sendOptions = SendOptions(adminAddress, DefaultGasProvider.GAS_LIMIT)
            val functionParams: List<Any> = listOf(cookieInfo.creatorAddress, cookieInfo.title, cookieInfo.content, cookieInfo.imageUrl, cookieInfo.tag, cookieInfo.hammerPrice)
            val receiptData = cookieContract.getMethod(CookieContractMethod.MINT_COOKIE_BY_OWNER.methodName).send(functionParams, sendOptions)

            // first transfered event index = 0
            val log = receiptData.logs[0]
            // event (from, to, tokenId) 인데... 첫번째 인자에 알수없는 값이 들어가있음. 그래서 tokenId는 index 가 3
            val cookieIdHex = log.topics[3]

            // 0x prefix 제거를 위해 substring
            getBigIntegerFromHexStr(cookieIdHex)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    @Throws(IOException::class)
    private fun getTransferLogs(): List<KlayLogs.LogResult<*>> {
        // FIXME: 매번 모든 Block 조회하는건 부하가 큼. 이벤트로그에대한 블록을 오프체인에서 관리하고, 그 이후 값에 대해서 조회하는 부분만 추가하는게 좋을듯
        val filter = KlayLogFilter(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST, cookieContract.contractAddress, null)
        val logs = cookieContract.getPastEvent(CookieContractEvent.TRANSFER.eventName, filter)
        return logs.logs
    }

    private fun getBigIntegerFromHexStr(cookieIdHex: String): BigInteger? {
        return BigInteger(cookieIdHex.substring(TRANSACTION_HEX_PREFIX_DIGIT_LENGTH), 16)
    }

    private fun fixAddressDigits(address: String): String? {
        require(address.length == TRANSACTION_ADDRESS_DIGIT_LENGTH)
        return address.substring(0, TRANSACTION_HEX_PREFIX_DIGIT_LENGTH) + address.substring(TRANSACTION_HEX_PREFIX_DIGIT_LENGTH + TRANSACTION_ZERONUM_DIGIT_LENGTH, TRANSACTION_ADDRESS_DIGIT_LENGTH)
    }

    private fun fromAddressNotZeroPredicate(): Predicate<KlayLogs.Log>? {
        return Predicate { log: KlayLogs.Log ->
            val fromAddress = log.topics[1]
            val result = getBigIntegerFromHexStr(fromAddress)
            result != BigInteger.ZERO
        }
    }
}
