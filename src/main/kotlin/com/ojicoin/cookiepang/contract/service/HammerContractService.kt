package com.ojicoin.cookiepang.contract.service

import com.klaytn.caver.abi.datatypes.Type
import com.klaytn.caver.contract.Contract
import com.klaytn.caver.contract.SendOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger
import java.util.Arrays

/**
 * @author seongchan.kang
 */
@Service
class HammerContractService(
    private val coinContract: Contract,
    @Value("\${contract.admin-address}") private val adminAddress: String,
    @Value("\${contract.addr.hammer}") private val hammerContractAddress: String,
) {

    fun getHammerContractAddress(): String = hammerContractAddress

    enum class CoinContractMethod(val methodName: String) {
        IS_MAX_APPROVED_ADDRESS("maxApprovedAddress"),
        BALANCE_OF("balanceOf"),
        SAFE_TRANSFER("safeTransfer");
    }

    // FIXME: 커스텀 익셉션 추가
    fun isMaxApprovedAddress(address: String): Boolean {
        return try {
            val callResult: List<Type<*>> =
                coinContract.call(CoinContractMethod.IS_MAX_APPROVED_ADDRESS.methodName, address)
            val result: Type<Boolean> = callResult[0] as Type<Boolean>
            result.value
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    fun balanceOf(address: String): BigInteger {
        return try {
            val callResult = coinContract.call(CoinContractMethod.BALANCE_OF.methodName, address)
            val result: Type<BigInteger> = callResult[0] as Type<BigInteger>
            result.value
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }

    // 이벤트로 망치 지급할때 사용 amount는 decimal 18 단위 기준 (즉, amount 10^18 = 1hammer)
    fun sendHammer(address: String, amount: BigInteger) {
        try {
            val sendOptions = SendOptions(adminAddress, DefaultGasProvider.GAS_LIMIT)
            val functionParams = Arrays.asList<Any>(address, amount)
            coinContract.getMethod(CoinContractMethod.SAFE_TRANSFER.methodName).send(functionParams, sendOptions)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }
}
