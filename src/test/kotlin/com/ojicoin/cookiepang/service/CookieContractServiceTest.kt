package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.dto.CookieEvent
import com.ojicoin.cookiepang.dto.CookieInfo
import com.ojicoin.cookiepang.dto.TransferInfo
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

    // FIXME: 테스트 어떻게!?

    @Test
    fun getTransferInfoByTxHash() {
        val transferInfo: TransferInfo = sut.getTransferInfoByTxHash("0x5e1a1d7a0d9c92638369738b1bd8ece38b630cbd26d3dc826c3a9f8257ab1f69")
        println(transferInfo)
    }

    @Test
    fun getContent() {
        val content: String = sut.getContent("1", "0xE84deA09A59C6b614c4934c46aa13ec56f6f3fEa")
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
        val isHide: Boolean = sut.isHide("1")
        println(isHide)
    }

    @Test
    fun isSale() {
        val isSale: Boolean = sut.isSale("1")
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
        val result: List<CookieEvent> = sut.getCookieEventsByNftTokenId(BigInteger.ONE)
        println(result)
    }

    @Test
    fun getCookieEventsByCookieIdWithBlockNum() {
        val result: List<CookieEvent> = sut.getCookieEventsByNftTokenId(DefaultBlockParameterNumber(83541764), BigInteger.ONE)
        println(result)
    }
}
