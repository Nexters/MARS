package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.dto.LoginRequest
import com.ojicoin.cookiepang.dto.LoginResponse
import com.ojicoin.cookiepang.dto.ProblemResponse
import com.ojicoin.cookiepang.service.AuthService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class LoginController(private val authService: AuthService) {
    @PostMapping("/login")
    @ApiResponses(
        value = [
            ApiResponse(description = "인증 성공", responseCode = "200"),
            ApiResponse(
                description = "지갑 주소가 중복된 경우",
                responseCode = "403",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    // TODO: 토큰 발급 후 토큰을 반환
    fun login(@RequestBody loginRequest: LoginRequest): LoginResponse = authService.login(request = loginRequest)
}
