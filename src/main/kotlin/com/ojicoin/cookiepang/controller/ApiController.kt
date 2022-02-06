package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.service.InquiryService
import com.ojicoin.cookiepang.service.UserTagService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.function.RequestPredicates.POST
import org.springframework.web.servlet.function.RouterFunctions.route
import org.springframework.web.servlet.function.ServerResponse.created
import org.springframework.web.servlet.function.body
import java.net.URI

@Controller
class ApiController(
    private val inquiryService: InquiryService,
    private val userTagService: UserTagService,
) {
    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/inquiries",
            operation = Operation(
                operationId = "inquiries",
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = InquiryRequestDto::class))]
                ),
                responses = [ApiResponse(responseCode = "200")]
            ),
        ),
        RouterOperation(
            path = "/users/{userId}/tags",
            operation = Operation(
                operationId = "createUserTags",
                parameters = [
                    Parameter(name = "userId", `in` = ParameterIn.PATH),
                ],
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = UserTagCreateDto::class))]
                ),
                responses = [ApiResponse(responseCode = "200")]
            ),
        )
    )
    fun create() = route(POST("/inquiries")) {
        // create inquiries
        val inquiryRequestDto = it.body<InquiryRequestDto>()

        inquiryService.create(inquiryRequestDto.title, inquiryRequestDto.senderUserId, inquiryRequestDto.receiverUserId)

        // TODO create certain uri path about created resource
        created(URI.create("")).build()
    }.andRoute(POST("/users/{userId}/tags")) {
        // create user interested tags
        val userId = it.pathVariable("userId").toLong()
        val userTagCreateDto = it.body<UserTagCreateDto>()

        userTagService.create(userId, userTagCreateDto.tagIdList)

        // TODO create certain uri path about created resource
        created(URI.create("")).build()
    }
}

data class InquiryRequestDto(
    val title: String,
    val senderUserId: Long,
    val receiverUserId: Long,
)

data class UserTagCreateDto(
    val tagIdList: List<Long>,
)
