package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.AskStatus.PENDING
import com.ojicoin.cookiepang.domain.Asks
import net.jqwik.api.Arbitraries
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

internal class AsksServiceTest(
    @Autowired val sut: AskService,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun create() {
        // given
        val asks = fixture.giveMeBuilder(Asks::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(PENDING))
            .sample()

        val create = sut.create(
            title = asks.title,
            senderUserId = asks.senderUserId,
            receiverUserId = asks.receiverUserId
        )

        then(asks.title).isEqualTo(create.title)
        then(asks.status).isEqualTo(create.status)
        then(asks.senderUserId).isEqualTo(create.senderUserId)
        then(asks.receiverUserId).isEqualTo(create.receiverUserId)
    }
}
