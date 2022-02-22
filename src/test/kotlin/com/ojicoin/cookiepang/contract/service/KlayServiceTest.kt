package com.ojicoin.cookiepang.contract.service

import com.ojicoin.cookiepang.SpringContextFixture
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigInteger

/**
 * @author seongchan.kang
 */
internal class KlayServiceTest(
    @Autowired val sut: KlayService
) : SpringContextFixture() {

    @Test
    fun getBalanceByAddress() {
        val balane: BigInteger = sut.getBalanceByAddress("0xE84deA09A59C6b614c4934c46aa13ec56f6f3fEa")
        println(balane)
    }
}
