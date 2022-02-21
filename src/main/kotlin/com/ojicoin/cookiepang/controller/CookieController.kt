package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.dto.CreateCookie
import com.ojicoin.cookiepang.dto.GetUserCookieTarget
import com.ojicoin.cookiepang.dto.GetUserCookieTarget.COLLECTED
import com.ojicoin.cookiepang.dto.GetUserCookieTarget.COOKIES
import com.ojicoin.cookiepang.dto.ProblemResponse
import com.ojicoin.cookiepang.dto.UpdateCookie
import com.ojicoin.cookiepang.service.CookieService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class CookieController(private val cookieService: CookieService) {
    @PostMapping("/cookies")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(
        value = [
            ApiResponse(description = "생성 성공", responseCode = "201"),
            ApiResponse(
                description = "중복",
                responseCode = "409",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            ),
            ApiResponse(
                description = "컨트랙 조회 실패",
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    fun createCookie(@RequestBody createCookie: CreateCookie): Cookie = cookieService.create(createCookie)

    @GetMapping("/users/{userId}/cookies")
    @ResponseStatus(HttpStatus.OK)
    fun getCookies(
        @PathVariable userId: Long,
        @RequestParam("target") target: GetUserCookieTarget,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "3") size: Int,
    ) = when (GetUserCookieTarget.valueOf(target.name.uppercase())) {
        COLLECTED -> cookieService.getOwnedCookies(
            userId = userId,
            viewUserId = userId,
            page = page,
            size = size
        )

        COOKIES -> cookieService.getAuthorCookies(
            userId = userId,
            viewUserId = userId,
            page = page,
            size = size
        )
    }

    @PutMapping("/cookies/{cookieId}")
    @ApiResponses(
        value = [
            ApiResponse(description = "변경 성공", responseCode = "201"),
            ApiResponse(
                description = "요청 실패 혹은 유효하지 않은 삭제 요청을 보낸 경우",
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    fun updateCookie(@PathVariable cookieId: Long, updateCookie: UpdateCookie) =
        cookieService.modify(cookieId = cookieId, updateCookie = updateCookie)

    @DeleteMapping("/cookies/{cookieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiResponses(
        value = [
            ApiResponse(description = "삭제 성공", responseCode = "204"),
            ApiResponse(
                description = "이미 지워진 쿠키",
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    fun deleteCookie(@PathVariable("cookieId") cookieId: Long) = cookieService.delete(cookieId)
}
