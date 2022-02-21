package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.dto.ProblemResponse
import com.ojicoin.cookiepang.service.ViewAssembler
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class ViewController(private val viewAssembler: ViewAssembler) {
    @GetMapping("/users/{userId}/cookies/{cookieId}/detail")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(
        value = [
            ApiResponse(description = "조회 성공", responseCode = "200"),
            ApiResponse(
                description = "디비에 존재하지 않음",
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    fun cookieDetailView(@PathVariable userId: Long, @PathVariable cookieId: Long) =
        viewAssembler.cookieView(viewerId = userId, cookieId = cookieId)

    @GetMapping("/users/{userId}/categories/all/cookies")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(
        value = [
            ApiResponse(description = "조회 성공", responseCode = "200"),
            ApiResponse(
                description = "디비에 존재하지 않음",
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    fun getAllCookies(
        @PathVariable userId: Long,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "3") size: Int,
    ) = viewAssembler.timelineView(viewerId = userId, page = page, size = size)

    @GetMapping("/users/{userId}/categories/{categoryId}/cookies")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(
        value = [
            ApiResponse(description = "조회 성공", responseCode = "200"),
            ApiResponse(
                description = "디비에 존재하지 않음",
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    fun getCookiesByCategory(
        @PathVariable userId: Long,
        @PathVariable categoryId: Long,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "3") size: Int,
    ) = viewAssembler.timelineView(viewerId = userId, viewCategoryId = categoryId, page = page, size = size)
}
