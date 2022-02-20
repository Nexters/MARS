package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.dto.CookieView
import com.ojicoin.cookiepang.dto.TimelineCookieView
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
                                array = ArraySchema(schema = Schema(implementation = TimelineCookieView::class))
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
                                array = ArraySchema(schema = Schema(implementation = TimelineCookieView::class))
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
