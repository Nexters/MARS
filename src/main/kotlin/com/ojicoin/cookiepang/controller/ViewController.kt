package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.service.ViewAssembler
import java.time.Instant
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.function.RequestPredicates.GET
import org.springframework.web.servlet.function.RouterFunctions.route
import org.springframework.web.servlet.function.ServerResponse.ok

@Controller
class ViewController(val viewAssembler: ViewAssembler) {
    @Bean
    fun cookieDetailView() = route(GET("/users/{userId}/cookies/{cookieId}/detail")) {
        val cookieId = it.pathVariable("cookieId").toLong()
        // TODO: rendering
        ok().body(viewAssembler.cookieView(cookieId = cookieId))
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
