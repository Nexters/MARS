package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.SpringContextFixture
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class FcmServiceTest(@Autowired private val sut: PushMessageOperations) : SpringContextFixture() {
    @Test
    fun send() {
        val actual = sut.send(
            "cCklTERBT_2BH_XrRO6epM:APA91bEC2k1Uwgu8XXWI7jkRRAjv70zGQdW8GY434c4JXr9As1U2ZafSxkHtqwGnxmSRBcYHXQrxYYLBAuntLmDhHvcQDqrndeTbiDjfB9L5QPKms8r3Bplj6xQATP2FT0sQJafSQV_R",
            PushMessageContent(
                title = "안녕하세요",
                body = "테스트 성공?? 쿠키팡팡 안드로이드 세팅 추가함 ",
                type = "hhh",
                image = null
            )
        ).messageId

        then(actual).isEqualTo("local")
    }
}
