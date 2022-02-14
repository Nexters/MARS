package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.AskStatus.ACCEPTED
import com.ojicoin.cookiepang.domain.AskStatus.DELETED
import com.ojicoin.cookiepang.domain.AskStatus.IGNORED
import com.ojicoin.cookiepang.domain.AskStatus.PENDING
import com.ojicoin.cookiepang.repository.AskRepository
import net.jqwik.api.Arbitraries
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

internal class AskServiceTest(
    @Autowired val sut: AskService,
    @Autowired val askRepository: AskRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun viewAboutSender() {
        val ask = fixture.giveMeBuilder(Ask::class.java)
            .setNull("id")
            .sample()

        askRepository.save(ask)

        val asksFromSender = sut.viewAboutSender(ask.senderUserId)

        val foundAsk = asksFromSender[0]
        then(ask.title).isEqualTo(foundAsk.title)
        then(ask.senderUserId).isEqualTo(foundAsk.senderUserId)
        then(ask.receiverUserId).isEqualTo(foundAsk.receiverUserId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun viewAboutReceiver() {
        val ask = fixture.giveMeBuilder(Ask::class.java)
            .setNull("id")
            .set("status", PENDING)
            .sample()

        askRepository.save(ask)

        val asksFromReceiver = sut.viewAboutReceiver(ask.receiverUserId)

        then(asksFromReceiver.size).isEqualTo(1)

        val foundAsk = asksFromReceiver[0]
        then(ask.title).isEqualTo(foundAsk.title)
        then(ask.senderUserId).isEqualTo(foundAsk.senderUserId)
        then(ask.receiverUserId).isEqualTo(foundAsk.receiverUserId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun viewAboutReceiverStatusNotPending() {
        val ask = fixture.giveMeBuilder(Ask::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(ACCEPTED, IGNORED, DELETED))
            .sample()

        askRepository.save(ask)

        val asksFromReceiver = sut.viewAboutReceiver(ask.receiverUserId)
        then(asksFromReceiver.size).isEqualTo(0)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun create() {
        // given
        val ask = fixture.giveMeBuilder(Ask::class.java)
            .setNull("id")
            .set("status", PENDING)
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
            .set("status", PENDING)
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

    @AfterEach
    fun tearDown() {
        askRepository.deleteAll()
    }
}
