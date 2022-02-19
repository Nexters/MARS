package com.ojicoin.cookiepang.contract.service

import com.ojicoin.cookiepang.SpringContextFixture
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigInteger

/**
 * @author seongchan.kang
 */
internal class CoinContractServiceTest(
    @Autowired val sut: CoinContractService
) : SpringContextFixture() {

    // FIXME: 테스트 어떻게!?

    @Test
    fun isMaxApprovedAddress() {
        val isMaxApprovedAddress: Boolean = sut.isMaxApprovedAddress("0xE84deA09A59C6b614c4934c46aa13ec56f6f3fEa")
        println(isMaxApprovedAddress)
    }

    @Test
    fun balanceOf() {
        val balance: BigInteger = sut.balanceOf("0xE84deA09A59C6b614c4934c46aa13ec56f6f3fEa")
        println(balance)
    }
}
