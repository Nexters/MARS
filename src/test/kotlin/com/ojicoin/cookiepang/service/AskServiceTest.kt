package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.AskStatus.ACCEPTED
import com.ojicoin.cookiepang.domain.AskStatus.DELETED
import com.ojicoin.cookiepang.domain.AskStatus.IGNORED
import com.ojicoin.cookiepang.domain.AskStatus.PENDING
import com.ojicoin.cookiepang.dto.UpdateAsk
import com.ojicoin.cookiepang.repository.AskRepository
import net.jqwik.api.Arbitraries
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

internal class AskServiceTest(
    @Autowired val sut: AskService,
    @Autowired val askRepository: AskRepository,
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
            receiverUserId = ask.receiverUserId
        )

        then(ask.title).isEqualTo(create.title)
        then(ask.status).isEqualTo(create.status)
        then(ask.senderUserId).isEqualTo(create.senderUserId)
        then(ask.receiverUserId).isEqualTo(create.receiverUserId)
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

        modifiedAsk.title.also { then(it).isEqualTo(updateAsk.title) }
        modifiedAsk.status.also { then(it).isEqualTo(updateAsk.status) }
    }

    @RepeatedTest(REPEAT_COUNT)
    fun modifyNotPendingStatusThrows() {
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
            .isExactlyInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("cannot update ask. This ask status is already changed or is deleted. status=${ask.status}")
    }
}
