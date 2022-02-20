package com.ojicoin.cookiepang.contract.controller

import com.ojicoin.cookiepang.contract.dto.Amount
import com.ojicoin.cookiepang.contract.dto.Answer
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

@RestController
@RequestMapping("/contract/cookies")
class ContractCookieController(
    private val cookieContractService: CookieContractService,
    private val userService: UserService,
) {

    // TODO getCookieInfos api (this is not implemented yet)

    @GetMapping("/contractAddress")
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

    @GetMapping("/{id}/hide")
    fun isHide(@PathVariable id: String): Answer {
        return Answer(
            answer = cookieContractService.isHide(nftTokenId = id.toBigInteger()),
        )
    }

    @GetMapping("/{id}/sale")
    fun isSale(@PathVariable id: String): Answer {
        return Answer(
            answer = cookieContractService.isSale(nftTokenId = id.toBigInteger()),
        )
    }

    @GetMapping("/{id}/price")
    fun getCookieHammerPrices(@RequestParam nftTokenId: String): Price {
        return Price(
            price = cookieContractService.getHammerPrice(nftTokenId = nftTokenId.toBigInteger()),
        )
    }

    @GetMapping("/users/{userId}/index")
    fun getNftTokenIdByCookieIndex(@PathVariable userId: String, @RequestParam index: String): TokenAddress {
        val user = userService.getById(userId.toLong())

        return TokenAddress(
            tokenAddress = cookieContractService.getNtfTokenIdByIndex(
                senderAddress = user.walletAddress,
                index = index.toBigInteger()
            )
        )
    }

    @GetMapping("/users/{userId}/count")
    fun getUserCookieCount(@PathVariable userId: String, @RequestParam index: String): Amount {
        val user = userService.getById(userId.toLong())

        return Amount(
            amount = cookieContractService.balanceOf(user.walletAddress)
        )
    }

    @GetMapping("/cookies/supply")
    fun getCookiesTotalSupply(): Amount {
        return Amount(
            amount = cookieContractService.totalSupply,
        )
    }
}
