package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.dto.CreateAsk
import com.ojicoin.cookiepang.dto.ProblemResponse
import com.ojicoin.cookiepang.dto.UpdateAsk
import com.ojicoin.cookiepang.service.AskService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AskController(
    private val askService: AskService,
) {
    @PostMapping("/asks")
    @ApiResponses(
        value = [
            ApiResponse(description = "생성 성공", responseCode = "200"),
            ApiResponse(
                description = "보낸 유저와 받는 유저가 같은 경우",
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    fun createAsks(@RequestBody createAsk: CreateAsk): Ask =
        askService.create(createAsk.title, createAsk.senderUserId, createAsk.receiverUserId, createAsk.categoryId)

    @PutMapping("/asks/{askId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(
        value = [
            ApiResponse(description = "변경 성공", responseCode = "200"),
            ApiResponse(
                description = "존재하지 않는 경우, 상태가 PENDING이 아닌 경우",
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    fun updateAsk(@PathVariable askId: Long, @RequestBody updateAsk: UpdateAsk) =
        askService.modify(id = askId, dto = updateAsk)
}
