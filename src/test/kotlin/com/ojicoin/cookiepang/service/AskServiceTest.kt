package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.AskStatus.PENDING
import net.jqwik.api.Arbitraries
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

internal class AskServiceTest(
    @Autowired val sut: AskService,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun create() {
        // given
        val ask = fixture.giveMeBuilder(Ask::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(PENDING))
            .sample()

        val create = sut.create(
            title = ask.title,
            senderUserId = ask.senderUserId,
            receiverUserId = ask.receiverUserId,
        )

        then(ask.title).isEqualTo(create.title)
        then(ask.status).isEqualTo(create.status)
        then(ask.senderUserId).isEqualTo(create.senderUserId)
        then(ask.receiverUserId).isEqualTo(create.receiverUserId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun createSameSenderReceiverUser() {
        // given
        val ask = fixture.giveMeBuilder(Ask::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(PENDING))
            .sample()

        thenThrownBy {
            sut.create(
                title = ask.title,
                senderUserId = ask.senderUserId,
                receiverUserId = ask.senderUserId,
            )
        }.isExactlyInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("senderUserId is same as receiverUserId. senderUserId=${ask.senderUserId}, receiverUserId=${ask.senderUserId}")
    }
}
