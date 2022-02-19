package com.ojicoin.cookiepang.contract.service

import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.contract.dto.CookieInfo
import com.ojicoin.cookiepang.contract.event.CookieEventLog
import com.ojicoin.cookiepang.contract.event.TransferEventLog
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.web3j.protocol.core.DefaultBlockParameterNumber
import java.math.BigInteger

/**
 * @author seongchan.kang
 */
internal class CookieContractServiceTest(
    @Autowired val sut: CookieContractService
) : SpringContextFixture() {

    @Test
    fun getTransferInfoByTxHash() {
        val transferEventLog: TransferEventLog = sut.getTransferEventLogByTxHash("0x3ebf627bc8736dc6d0c81ba4a8afc0d6ffdc5d6647f511adbedcdd5645981b51")
        println(transferEventLog)
    }

    @Test
    fun getContent() {
        val content: String = sut.getContent(BigInteger.ONE, "0xE84deA09A59C6b614c4934c46aa13ec56f6f3fEa")
        println(content)
    }

    @Test
    fun getMintingPriceForHammer() {
        val mintingPriceForHammer: BigInteger = sut.mintingPriceForHammer
        println(mintingPriceForHammer)
    }

    @Test
    fun getMintingPriceForKlaytn() {
        val mintingPriceForKlaytn: BigInteger = sut.mintingPriceForKlaytn
        println(mintingPriceForKlaytn)
    }

    @Test
    fun getOwnedCookieIds() {
        val cookieIds: List<String> = sut.getOwnedNtfTokenIds("0xE84deA09A59C6b614c4934c46aa13ec56f6f3fEa")
        println(cookieIds)
    }

    @Test
    fun balanceOf() {
        val balance: BigInteger = sut.balanceOf("0xE84deA09A59C6b614c4934c46aa13ec56f6f3fEa")
        println(balance)
    }

    @Test
    fun isHide() {
        val isHide: Boolean = sut.isHide(BigInteger.ONE)
        println(isHide)
    }

    @Test
    fun isSale() {
        val isSale: Boolean = sut.isSale(BigInteger.ONE)
        println(isSale)
    }

    @Test
    fun getHammerPrice() {
        val hammerPrice: BigInteger = sut.getHammerPrice(BigInteger.ONE)
        println(hammerPrice)
    }

    @Test
    fun getCookieIdByIndex() {
        val cookieIdByIndex: BigInteger = sut.getNtfTokenIdByIndex("0xE84deA09A59C6b614c4934c46aa13ec56f6f3fEa", BigInteger.valueOf(0))
        println(cookieIdByIndex)
    }

    @Test
    fun getTotalSupply() {
        val totalSupply: BigInteger = sut.totalSupply
        println(totalSupply)
    }

    @Test
    fun createDefaultCookies() {

        val createdDefaultCookies: List<BigInteger> = sut.createDefaultCookies(
            listOf(
                CookieInfo(
                    "0xe3F744017BB487F88B1CE9587FfD672E9F306769",
                    "test",
                    "test",
                    "test",
                    "test",
                    BigInteger.valueOf(10)
                ),
                CookieInfo(
                    "0xe3F744017BB487F88B1CE9587FfD672E9F306769",
                    "test2",
                    "test2",
                    "test2",
                    "tes2",
                    BigInteger.valueOf(20)
                )
            )
        ) as List<BigInteger>

        println(createdDefaultCookies)
    }

    @Test
    fun getCookieEventsByCookieId() {
        val result: List<CookieEventLog> = sut.getCookieEventLogByNftTokenId(BigInteger.ONE)
        println(result)
    }

    @Test
    fun getCookieEventsByCookieIdWithBlockNum() {
        val result: List<CookieEventLog> = sut.getCookieEventLogByNftTokenId(DefaultBlockParameterNumber(83541764), BigInteger.ONE)
        println(result)
    }
}
