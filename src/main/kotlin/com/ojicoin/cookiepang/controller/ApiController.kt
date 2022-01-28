package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.service.CookieService
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.function.RequestPredicates.GET
import org.springframework.web.servlet.function.RouterFunctions.route
import org.springframework.web.servlet.function.ServerResponse.ok

@Controller
class ApiController(private val cookieService: CookieService) {
    @Bean
    fun viewCookie() = route(GET("/users/{userId}/cookies/{cookieId}")) {
        val userId = it.pathVariable("userId").toLong()
        val cookieId = it.pathVariable("cookieId").toLong()
        val cookie = cookieService.view(userId = userId, cookieId = cookieId)
        ok().body(cookie) // TODO: cookieView 추가, controller 테스트
    }
}
