package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.exception.UserExistException
import com.ojicoin.cookiepang.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class LoginController(
    private val userService: UserService,
) {
    @GetMapping("/auth")
    fun auth(@RequestParam("walletAddress") walletAddress: String?): ResponseEntity<String> {
        if (walletAddress == null) {
            return ResponseEntity.badRequest().body("empty walletAddress in parameters")
        }

        try {
            userService.checkDuplicateUser(walletAddress = walletAddress)
        } catch (e: UserExistException) {
            return ResponseEntity.status(409)
                .body(e.message)
        }
        return ResponseEntity.ok().build()
    }
}
