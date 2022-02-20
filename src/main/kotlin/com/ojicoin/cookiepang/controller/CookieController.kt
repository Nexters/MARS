package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.controller.GetUserCookieTarget.COLLECTED
import com.ojicoin.cookiepang.controller.GetUserCookieTarget.COOKIES
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.dto.CreateCookie
import com.ojicoin.cookiepang.dto.UpdateCookie
import com.ojicoin.cookiepang.service.CookieService
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
    fun updateCookie(@PathVariable cookieId: Long, updateCookie: UpdateCookie) =
        cookieService.modify(cookieId = cookieId, updateCookie = updateCookie)

    @DeleteMapping("/cookies/{cookieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCookie(@PathVariable("cookieId") cookieId: Long) = cookieService.delete(cookieId)
}
