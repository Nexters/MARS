package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.service.ViewAssembler
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.function.RequestPredicates.GET
import org.springframework.web.servlet.function.RouterFunctions.route
import org.springframework.web.servlet.function.ServerResponse.ok
import java.time.Instant

@Controller
class ViewController(private val viewAssembler: ViewAssembler) {
    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/users/{userId}/cookies/{cookieId}/detail",
            operation = Operation(
                operationId = "viewCookieDetail",
                parameters = [
                    Parameter(name = "userId", `in` = PATH),
                    Parameter(name = "cookieId", `in` = PATH),
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        content = [Content(schema = Schema(implementation = CookieView::class))]
                    )
                ]
            ),
        )
    )
    fun views() = route(GET("/users/{userId}/cookies/{cookieId}/detail")) {
        val userId = it.pathVariable("userId").toLong()
        val cookieId = it.pathVariable("cookieId").toLong()
        ok().body(viewAssembler.cookieView(viewerId = userId, cookieId = cookieId))
    }
}

data class CookieView(
    val question: String,
    val answer: String?,
    val collectorName: String,
    val creatorName: String,
    val contractAddress: String,
    val tokenAddress: String,
    val viewCount: Long,
    val price: Long,
    val histories: List<CookieHistory>,
)

data class CookieHistory(
    val action: Action,
    val content: String,
    val createdAt: Instant,
)

enum class Action {
    MODIFY,
    BUY,
    CREATE
}
