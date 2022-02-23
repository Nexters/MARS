package com.ojicoin.cookiepang.contract.controller

import com.ojicoin.cookiepang.contract.dto.Answer
import com.ojicoin.cookiepang.contract.dto.Balance
import com.ojicoin.cookiepang.contract.dto.ContractAddress
import com.ojicoin.cookiepang.contract.service.HammerContractService
import com.ojicoin.cookiepang.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/contract/hammers")
class ContractHammerController(
    private val hammerContractService: HammerContractService,
    private val userService: UserService,
) {

    @GetMapping("/address")
    fun getCookiesContractAddress(): ContractAddress = ContractAddress(
        address = hammerContractService.getHammerContractAddress()
    )

    @GetMapping("/users/{userId}/approve")
    fun isMaxApprovedAddress(@PathVariable userId: Long): Answer {
        val user = userService.getById(userId)

        return Answer(
            answer = hammerContractService.isMaxApprovedAddress(user.walletAddress)
        )
    }

    @GetMapping("/users/{userId}/balance")
    fun getUserHammerCount(@PathVariable userId: Long): Balance {
        val user = userService.getById(userId)

        return Balance(
            balance = hammerContractService.balanceOf(user.walletAddress)
        )
    }
}
