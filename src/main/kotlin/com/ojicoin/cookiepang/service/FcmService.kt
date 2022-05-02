package com.ojicoin.cookiepang.service

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient

@Service
@Profile("real")
class FcmService(
    @Value("\${google-fcm.url}") private val url: String,
    private val tokenGenerator: TokenGenerator,
) : PushMessageOperations {
    private val webClient: WebClient = WebClient.builder()
        .baseUrl(url)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .build()

    override fun send(
        destination: String,
        message: PushMessageContent,
    ): PushMessageResponse = webClient.post()
        .uri("/messages:send")
        .header(
            HttpHeaders.AUTHORIZATION,
            "Bearer ${tokenGenerator.fcmToken}"
        ) // 토큰이 만료된 경우 자동으로 갱신하기 위해 매번 조회함. 내부적으로 캐싱함.
        .body(
            BodyInserters.fromValue(
                FcmMessageWrapper(
                    FcmMessage(
                        receiverToken = destination,
                        pushMessageContent = message,
                    )
                )
            )
        )
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, ClientResponse::createException)
        .bodyToMono(PushMessageResponse::class.java)
        .blockOptional()
        .orElseThrow()
}

@Service
@ConditionalOnMissingBean(value = [PushMessageOperations::class], ignored = [LocalPushMessageService::class])
class LocalPushMessageService : PushMessageOperations {
    override fun send(destination: String, message: PushMessageContent): PushMessageResponse =
        PushMessageResponse("local")
}

data class FcmMessageWrapper(val message: FcmMessage)

data class FcmMessageAndroidSetting(val ttl: String = "604800s", val priority: String = "normal")

data class FcmMessage(
    @JsonProperty("token") val receiverToken: String,
    @JsonProperty("data") val pushMessageContent: PushMessageContent,
    val android: FcmMessageAndroidSetting = FcmMessageAndroidSetting()
)
