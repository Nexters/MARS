package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.AskStatus.ACCEPTED
import com.ojicoin.cookiepang.domain.AskStatus.DELETED
import com.ojicoin.cookiepang.domain.AskStatus.IGNORED
import com.ojicoin.cookiepang.domain.AskStatus.PENDING
import com.ojicoin.cookiepang.dto.UpdateAsk
import com.ojicoin.cookiepang.exception.InvalidDomainStatusException
import com.ojicoin.cookiepang.exception.InvalidRequestException
import com.ojicoin.cookiepang.repository.AskRepository
import com.ojicoin.cookiepang.repository.NotificationRepository
import net.jqwik.api.Arbitraries
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable

internal class AskServiceTest(
    @Autowired val sut: AskService,
    @Autowired val askRepository: AskRepository,
    @Autowired val notificationRepository: NotificationRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun viewAboutSender() {
        val ask = fixture.giveMeBuilder(Ask::class.java)
            .setNull("id")
            .sample()

        askRepository.save(ask)

        val asksFromSender = sut.getBySenderId(ask.senderId, Pageable.unpaged())

        val foundAsk = asksFromSender[0]
        then(ask.title).isEqualTo(foundAsk.title)
        then(ask.senderId).isEqualTo(foundAsk.senderId)
        then(ask.receiverId).isEqualTo(foundAsk.receiverId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun viewAboutReceiver() {
        val ask = fixture.giveMeBuilder(Ask::class.java)
            .setNull("id")
            .set("status", PENDING)
            .sample()

        askRepository.save(ask)

        val asksFromReceiver = sut.getByReceiverId(ask.receiverId, Pageable.unpaged())

        then(asksFromReceiver.size).isEqualTo(1)

        val foundAsk = asksFromReceiver[0]
        then(ask.title).isEqualTo(foundAsk.title)
        then(ask.senderId).isEqualTo(foundAsk.senderId)
        then(ask.receiverId).isEqualTo(foundAsk.receiverId)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun viewAboutReceiverStatusNotPending() {
        val ask = fixture.giveMeBuilder(Ask::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(ACCEPTED, IGNORED, DELETED))
            .sample()

        askRepository.save(ask)

        val asksFromReceiver = sut.getByReceiverId(ask.receiverId, Pageable.unpaged())
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
            senderId = ask.senderId,
            receiverId = ask.receiverId,
            categoryId = ask.categoryId,
        )

        then(ask.title).isEqualTo(create.title)
        then(ask.status).isEqualTo(create.status)
        then(ask.senderId).isEqualTo(create.senderId)
        then(ask.receiverId).isEqualTo(create.receiverId)

        // test about notification
        then(notificationRepository.findAll()).hasSize(1)
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
                senderId = ask.senderId,
                receiverId = ask.senderId,
                categoryId = ask.categoryId,
            )
        }.isExactlyInstanceOf(InvalidRequestException::class.java)
            .hasMessageContaining("senderId is same as receiverId.")
    }

    @RepeatedTest(REPEAT_COUNT)
    fun modify() {
        // given
        val ask = fixture.giveMeBuilder(Ask::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(PENDING))
            .sample()
        val savedAskId = askRepository.save(ask).id!!

        val updateAsk = fixture.giveMeBuilder(UpdateAsk::class.java)
            .set("status", Arbitraries.of(ACCEPTED, IGNORED, DELETED))
            .sample()
        val modifiedAsk = sut.modify(id = savedAskId, dto = updateAsk)

        updateAsk.title?.also { then(it).isEqualTo(modifiedAsk.title) }
        updateAsk.status?.also { then(it).isEqualTo(modifiedAsk.status) }
        updateAsk.categoryId?.also { then(it).isEqualTo(modifiedAsk.categoryId) }
    }

    @RepeatedTest(REPEAT_COUNT)
    fun modifyThrowNotPendingStatus() {
        // given
        val ask = fixture.giveMeBuilder(Ask::class.java)
            .setNull("id")
            .set("status", Arbitraries.of(ACCEPTED, IGNORED, DELETED))
            .sample()

        val savedAskId = askRepository.save(ask).id!!
        val updateAsk = fixture.giveMeBuilder(UpdateAsk::class.java)
            .set("status", Arbitraries.of(ACCEPTED, IGNORED, DELETED))
            .sample()

        thenThrownBy { sut.modify(id = savedAskId, dto = updateAsk) }
            .isExactlyInstanceOf(InvalidDomainStatusException::class.java)
            .hasMessageContaining("cannot update ask. This ask status is already changed or is deleted.")
    }

    @AfterEach
    fun tearDown() {
        notificationRepository.deleteAll()
        askRepository.deleteAll()
    }
}
