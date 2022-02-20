package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.exception.UserExistException
import com.ojicoin.cookiepang.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.function.RequestPredicates.GET
import org.springframework.web.servlet.function.RouterFunctions.route
import org.springframework.web.servlet.function.ServerResponse.badRequest
import org.springframework.web.servlet.function.ServerResponse.ok
import org.springframework.web.servlet.function.ServerResponse.status

@Controller
class LoginController(
    private val userService: UserService
) {

    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/auth",
            operation = Operation(
                operationId = "checkDuplicateUser",
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        description = "There is not duplicate user.",
                    ),
                    ApiResponse(
                        responseCode = "409",
                        description = "There is duplicate user",
                        content = [
                            Content(
                                mediaType = "text/plain",
                                schema = Schema(implementation = String::class)
                            )
                        ]
                    )
                ]
            )
        )
    )
    fun auth() = route(GET("/auth")) {
        // todo add auth process

        // 이전에 회원가입을 했는지 확인
        val walletAddress = it.param("walletAddress")
        if (walletAddress.isEmpty) {
            return@route badRequest().body("empty walletAddress in parameters")
        }

        try {
            userService.checkDuplicateUser(walletAddress = walletAddress.get())
        } catch (e: UserExistException) {
            return@route status(CONFLICT).body(e.message!!)
        }

        ok().build()
    }
}
