package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.controller.GetAskTarget.RECEIVER
import com.ojicoin.cookiepang.controller.GetAskTarget.SENDER
import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.dto.CreateAsk
import com.ojicoin.cookiepang.dto.UpdateAsk
import com.ojicoin.cookiepang.service.AskService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class AskController(private val askService: AskService) {
    @PostMapping("/asks")
    fun createAsks(createAsk: CreateAsk): Ask =
        askService.create(createAsk.title, createAsk.senderUserId, createAsk.receiverUserId)

    @GetMapping("/users/{userId}/asks")
    @ResponseStatus(HttpStatus.OK)
    fun getAsks(@PathVariable userId: Long, @RequestParam("target") target: GetAskTarget) =
        when (GetAskTarget.valueOf(target.name.uppercase())) {
            SENDER -> askService.viewAboutSender(userId = userId)
            RECEIVER -> askService.viewAboutReceiver(userId = userId)
        }

    @PutMapping("/asks/{askId}")
    @ResponseStatus(HttpStatus.OK)
    fun updateAsk(@PathVariable askId: Long, updateAsk: UpdateAsk) = askService.modify(id = askId, dto = updateAsk)
}

enum class GetAskTarget { SENDER, RECEIVER }
