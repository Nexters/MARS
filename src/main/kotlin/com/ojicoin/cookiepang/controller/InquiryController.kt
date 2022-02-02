package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.service.InquiryService
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.function.RequestPredicates.POST
import org.springframework.web.servlet.function.RouterFunctions.route
import org.springframework.web.servlet.function.ServerResponse.created
import org.springframework.web.servlet.function.body
import java.net.URI

@Controller
class InquiryController(
    private val inquiryService: InquiryService
) {

    @Bean
    fun createUserTags() = route(POST("/inquiries")) {
        val inquiryRequestDto = it.body<InquiryRequestDto>()

        inquiryService.create(inquiryRequestDto.title, inquiryRequestDto.senderUserId, inquiryRequestDto.receiverUserId)

        // TODO create certain uri path about created resource
        created(URI.create("")).build()
    }
}

data class InquiryRequestDto(
    val title: String,
    val senderUserId: Long,
    val receiverUserId: Long
)
