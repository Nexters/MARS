package com.ojicoin.cookiepang.service

import com.klaytn.caver.abi.datatypes.Type
import com.klaytn.caver.contract.Contract
import com.klaytn.caver.methods.request.CallObject
import org.springframework.stereotype.Service

/**
 * @author seongchan.kang
 */
@Service
class CoinContractService(
    private val coinContract: Contract
) {

    enum class CoinContractMethod(val methodName: String) {
        IS_MAX_APPROVED_ADDRESS("maxApprovedAddress");
    }

    fun Contract.call(callObject: CallObject, event: CoinContractService.CoinContractMethod, vararg any: Any) = this.call(callObject, event.methodName, any)
    fun Contract.call(event: CoinContractService.CoinContractMethod, vararg any: Any) = this.call(event.methodName, any)

    // FIXME: 커스텀 익셉션 추가
    fun isMaxApprovedAddress(address: String): Boolean {
        return try {
            val callResult: List<Type<*>> = coinContract.call(CoinContractMethod.IS_MAX_APPROVED_ADDRESS, address)
            val result: Type<Boolean> = callResult[0] as Type<Boolean>
            result.value
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException()
        }
    }
}
