package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.service.UserTagService
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.function.RequestPredicates.POST
import org.springframework.web.servlet.function.RouterFunctions.route
import org.springframework.web.servlet.function.ServerResponse.created
import org.springframework.web.servlet.function.body
import java.net.URI

@Controller
class UserTagController(
    private val userTagService: UserTagService
) {

    @Bean
    fun createUserTags() = route(POST("/users/{userId}/tags")) {
        val userId = it.pathVariable("userId").toLong()
        val userTagCreateDto = it.body<UserTagCreateDto>()

        userTagService.create(userId, userTagCreateDto.tagList)

        // TODO create certain uri path about created resource
        created(URI.create("")).build()
    }
}

data class UserTagCreateDto(
    val tagList: List<Long>
)
