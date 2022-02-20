package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.domain.Action
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.service.ViewAssembler
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.ArraySchema
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
import java.math.BigInteger
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
                    Parameter(name = "userId", `in` = ParameterIn.PATH),
                    Parameter(name = "cookieId", `in` = ParameterIn.PATH),
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        content = [Content(schema = Schema(implementation = CookieView::class))]
                    )
                ]
            ),
        ),
        RouterOperation(
            path = "/users/{userId}/categories/all/cookies",
            operation = Operation(
                operationId = "getCookiesByAllCategory",
                parameters = [
                    Parameter(name = "userId", `in` = ParameterIn.PATH),
                    Parameter(
                        name = "page",
                        schema = Schema(implementation = Int::class, defaultValue = "0"),
                        `in` = ParameterIn.QUERY
                    ),
                    Parameter(
                        name = "size",
                        schema = Schema(implementation = Int::class, defaultValue = "3"),
                        `in` = ParameterIn.QUERY
                    ),
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        content = [
                            Content(
                                mediaType = "application/json",
                                array = ArraySchema(schema = Schema(implementation = Cookie::class))
                            )
                        ]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/{userId}/categories/{categoryId}/cookies",
            operation = Operation(
                operationId = "getCookiesByCategory",
                parameters = [
                    Parameter(name = "userId", `in` = ParameterIn.PATH),
                    Parameter(name = "categoryId", `in` = ParameterIn.PATH),
                    Parameter(
                        name = "page",
                        schema = Schema(implementation = Int::class, defaultValue = "0"),
                        `in` = ParameterIn.QUERY
                    ),
                    Parameter(
                        name = "size",
                        schema = Schema(implementation = Int::class, defaultValue = "3"),
                        `in` = ParameterIn.QUERY
                    ),
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        content = [
                            Content(
                                mediaType = "application/json",
                                array = ArraySchema(schema = Schema(implementation = Cookie::class))
                            )
                        ]
                    )
                ]
            )
        ),
    )
    fun views() = route(GET("/users/{userId}/cookies/{cookieId}/detail")) {
        val userId = it.pathVariable("userId").toLong()
        val cookieId = it.pathVariable("cookieId").toLong()
        ok().body(viewAssembler.cookieView(viewerId = userId, cookieId = cookieId))
    }.andRoute(GET("/users/{userId}/categories/all/cookies")) {
        val userId = it.pathVariable("userId").toLong()
        val page = it.param("page").map { page -> page.toInt() }.orElse(0)
        val size = it.param("size").map { size -> size.toInt() }.orElse(3)
        ok().body(viewAssembler.timelineView(viewerId = userId, page = page, size = size))
    }.andRoute(GET("/users/{userId}/categories/{categoryId}/cookies")) {
        val categoryId = it.pathVariable("categoryId").toLong()
        val userId = it.pathVariable("userId").toLong()
        val page = it.param("page").map { page -> page.toInt() }.orElse(0)
        val size = it.param("size").map { size -> size.toInt() }.orElse(3)
        ok().body(viewAssembler.timelineView(viewerId = userId, categoryId = categoryId, page = page, size = size))
    }
}

data class CookieView(
    val question: String,
    val answer: String?,
    val collectorName: String,
    val collectorProfileUrl: String?,
    val creatorName: String,
    val creatorProfileUrl: String?,
    val contractAddress: String,
    val nftTokenId: BigInteger,
    val viewCount: Long,
    val price: Long,
    val histories: List<CookieHistoryView>,
    val myCookie: Boolean,
)

data class CookieHistoryView(
    val action: Action,
    val content: String,
    val createdAt: Instant,
)

data class TimelineCookieView(
    val cookieId: Long,
    val collectorProfileUrl: String?,
    val collectorName: String,
    val question: String,
    val answer: String?,
    val contractAddress: String,
    val nftTokenId: BigInteger,
    val viewCount: Long,
    val cookieImageUrl: String?,
    val price: Long,
    val myCookie: Boolean,
    val createdAt: Instant,
)
