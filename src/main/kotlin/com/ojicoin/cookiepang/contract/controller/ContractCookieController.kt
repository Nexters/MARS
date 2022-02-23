package com.ojicoin.cookiepang.contract.controller

import com.ojicoin.cookiepang.contract.dto.Amount
import com.ojicoin.cookiepang.contract.dto.Answer
import com.ojicoin.cookiepang.contract.dto.Balance
import com.ojicoin.cookiepang.contract.dto.ContractAddress
import com.ojicoin.cookiepang.contract.dto.Price
import com.ojicoin.cookiepang.contract.dto.TokenAddress
import com.ojicoin.cookiepang.contract.service.CookieContractService
import com.ojicoin.cookiepang.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.math.BigInteger

@RestController
@RequestMapping("/contract/cookies")
class ContractCookieController(
    private val cookieContractService: CookieContractService,
    private val userService: UserService,
) {

    // TODO getCookieInfos api (this is not implemented yet)

    @GetMapping("/address")
    fun getCookiesContractAddress(): ContractAddress = ContractAddress(
        address = cookieContractService.getCookieContractAddress()
    )

    @GetMapping("/prices/hammer")
    fun getMintingPriceForHammer(): Price {
        return Price(
            price = cookieContractService.mintingPriceForHammer,
        )
    }

    @GetMapping("/prices/klaytn")
    fun getMintingPriceForKlaytn(): Price {
        return Price(
            price = cookieContractService.mintingPriceForKlaytn,
        )
    }

    @GetMapping("/{nftTokenId}/hide")
    fun isHide(@PathVariable nftTokenId: BigInteger): Answer {
        return Answer(
            answer = cookieContractService.isHide(nftTokenId = nftTokenId),
        )
    }

    @GetMapping("/{nftTokenId}/sale")
    fun isSale(@PathVariable nftTokenId: BigInteger): Answer {
        return Answer(
            answer = cookieContractService.isSale(nftTokenId = nftTokenId),
        )
    }

    @GetMapping("/{nftTokenId}/price")
    fun getCookieHammerPrices(@PathVariable nftTokenId: BigInteger): Price {
        return Price(
            price = cookieContractService.getHammerPrice(nftTokenId = nftTokenId),
        )
    }

    @GetMapping("/users/{userId}/nftTokenId")
    fun getNftTokenIdByCookieIndex(@PathVariable userId: Long, @RequestParam index: String): TokenAddress {
        val user = userService.getById(userId)

        return TokenAddress(
            tokenAddress = cookieContractService.getNtfTokenIdByIndex(
                senderAddress = user.walletAddress,
                index = index.toBigInteger()
            )
        )
    }

    @GetMapping("/users/{userId}/balance")
    fun getUserCookieCount(@PathVariable userId: Long): Balance {
        val user = userService.getById(userId)

        return Balance(
            balance = cookieContractService.balanceOf(user.walletAddress)
        )
    }

    @GetMapping("/cookies/supply")
    fun getCookiesTotalSupply(): Amount {
        return Amount(
            amount = cookieContractService.totalSupply,
        )
    }
}
