package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.dto.ProblemResponse
import com.ojicoin.cookiepang.service.UserService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class LoginController(
    private val userService: UserService,
) {
    @GetMapping("/auth")
    @ApiResponses(
        value = [
            ApiResponse(description = "인증 성공", responseCode = "200"),
            ApiResponse(
                description = "지갑 주소가 중복된 경우",
                responseCode = "409",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    fun auth(@RequestParam("walletAddress") walletAddress: String): ResponseEntity<String> {
        userService.checkDuplicateUser(walletAddress = walletAddress)
        return ResponseEntity.ok().build()
    }
}
