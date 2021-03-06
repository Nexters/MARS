package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.controller.GetAskTarget.RECEIVER
import com.ojicoin.cookiepang.controller.GetAskTarget.SENDER
import com.ojicoin.cookiepang.dto.GetUserCookieTarget
import com.ojicoin.cookiepang.dto.GetUserCookieTarget.AUTHOR
import com.ojicoin.cookiepang.dto.GetUserCookieTarget.OWNED
import com.ojicoin.cookiepang.dto.PageableView
import com.ojicoin.cookiepang.dto.ProblemResponse
import com.ojicoin.cookiepang.dto.UserCookieView
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
            ),
            ApiResponse(
                description = "권한이 없는 유저가 숨겨진 쿠키를 조회",
                responseCode = "403",
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

    @GetMapping("/users/{userId}/cookies")
    @ResponseStatus(HttpStatus.OK)
    fun getUserCookies(
        @PathVariable userId: Long,
        @RequestParam("target") target: GetUserCookieTarget,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "3") size: Int,
    ): PageableView<UserCookieView> = when (target) {
        OWNED -> viewAssembler.ownedCookiesView(
            userId = userId,
            page = page,
            size = size
        )

        AUTHOR -> viewAssembler.authorCookiesView(
            userId = userId,
            page = page,
            size = size
        )
    }

    @GetMapping("/users/{userId}/asks")
    @ResponseStatus(HttpStatus.OK)
    fun getAsks(
        @PathVariable userId: Long,
        @RequestParam("target") target: GetAskTarget,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "3") size: Int,
    ) = when (target) {
        SENDER -> viewAssembler.askViewAboutSender(userId = userId, page = page, size = size)
        RECEIVER -> viewAssembler.askViewAboutReceiver(userId = userId, page = page, size = size)
    }
}

enum class GetAskTarget { SENDER, RECEIVER }
