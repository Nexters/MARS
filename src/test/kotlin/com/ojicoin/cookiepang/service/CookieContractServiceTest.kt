package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.dto.CookieInfo
import com.ojicoin.cookiepang.dto.TransferInfo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
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
        val transferInfo: TransferInfo = sut.getTransferInfoByTxHash("0x0f92242abcd881cea5ecc2642f938a76200d41e21f5b4f5705332ecdd9bccccb")
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
        val cookieIds: List<String> = sut.getOwnedCookieIds("0xE84deA09A59C6b614c4934c46aa13ec56f6f3fEa")
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
        val hammerPrice: BigInteger = sut.getHammerPrice("1")
        println(hammerPrice)
    }

    @Test
    fun getCookieIdByIndex() {
        val cookieIdByIndex: BigInteger = sut.getCookieIdByIndex("0xE84deA09A59C6b614c4934c46aa13ec56f6f3fEa", BigInteger.valueOf(0))
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
}
