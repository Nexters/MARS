package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.contract.service.CookieContractService
import com.ojicoin.cookiepang.dto.Amount
import com.ojicoin.cookiepang.dto.Answer
import com.ojicoin.cookiepang.dto.Price
import com.ojicoin.cookiepang.dto.TokenAddress
import com.ojicoin.cookiepang.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ContractController(
    private val cookieContractService: CookieContractService,
    private val userService: UserService,
) {

    // TODO getCookieInfos api (this is not implemented yet)

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

    @GetMapping("/cookies/is/hide")
    fun isHide(@RequestParam nftTokenId: String): Answer {
        return Answer(
            answer = cookieContractService.isHide(nftTokenId = nftTokenId.toBigInteger()),
        )
    }

    @GetMapping("/cookies/is/sale")
    fun isSale(@RequestParam nftTokenId: String): Answer {
        return Answer(
            answer = cookieContractService.isSale(nftTokenId = nftTokenId.toBigInteger()),
        )
    }

    @GetMapping("/cookies/prices")
    fun getCookieHammerPrices(@RequestParam nftTokenId: String): Price {
        return Price(
            price = cookieContractService.getHammerPrice(nftTokenId = nftTokenId.toBigInteger()),
        )
    }

    @GetMapping("/users/{userId}/nfttokenid")
    fun getNftTokenIdByCookieIndex(@PathVariable userId: String, @RequestParam index: String): TokenAddress {
        val user = userService.getById(userId.toLong())

        return TokenAddress(
            tokenAddress = cookieContractService.getNtfTokenIdByIndex(
                senderAddress = user.walletAddress,
                index = index.toBigInteger()
            )
        )
    }

    @GetMapping("/cookies/supply")
    fun getCookiesTotalSupply(): Amount {
        return Amount(
            amount = cookieContractService.totalSupply,
        )
    }
}
