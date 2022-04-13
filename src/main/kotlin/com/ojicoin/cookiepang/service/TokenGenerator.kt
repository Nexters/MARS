package com.ojicoin.cookiepang.service

import com.google.auth.oauth2.GoogleCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

@Component
class TokenGenerator(@Value("\${google-fcm.key-path}") private val fcmKeyPath: String) {
    val fcmToken: String
        get() = GoogleCredentials // 내부적으로 캐싱 되있음
            .fromStream(ClassPathResource(fcmKeyPath).inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform")).also { it.refreshIfExpired() }
            .accessToken.tokenValue
}
