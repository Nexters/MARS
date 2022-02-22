package com.ojicoin.cookiepang.contract.controller

import com.ojicoin.cookiepang.contract.dto.Balance
import com.ojicoin.cookiepang.contract.service.KlayService
import com.ojicoin.cookiepang.service.UserService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author seongchan.kang
 */
@RestController
@RequestMapping("/contract/klay")
internal class KlayController(
    private val klayService: KlayService,
    private val userService: UserService,
) {

    @GetMapping("/users/{userId}/balance")
    fun getBalance(@PathVariable userId: Long): Balance {
        val user = userService.getById(userId)
        return Balance(
            balance = klayService.getBalanceByAddress(user.walletAddress)
        )
    }
}
