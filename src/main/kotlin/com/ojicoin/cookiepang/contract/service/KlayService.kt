package com.ojicoin.cookiepang.contract.service

import com.klaytn.caver.Caver
import org.springframework.stereotype.Service
import java.math.BigInteger

/**
 * @author seongchan.kang
 */
@Service
class KlayService(
    private val caver: Caver
) {
    fun getBalanceByAddress(address: String): BigInteger {
        return caver.rpc.klay.getBalance(address).sendAsync().get().getValue()
    }
}
