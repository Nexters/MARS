package com.ojicoin.cookiepang.service

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Inquiry
import com.ojicoin.cookiepang.repository.InquiryRepository
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class InquiryServiceTest(
    @Autowired val inquiryRepository: InquiryRepository,
    @Autowired val sut: InquiryService,
) : SpringContextFixture() {

    @Test
    fun create() {
        // given
        val inquiry = fixture.giveMeBuilder<Inquiry>()
            .setNull("id")
            .sample()

        val create = sut.create(
            title = inquiry.title,
            senderUserId = inquiry.senderUserId,
            receiverUserId = inquiry.receiverUserId
        )
        val expected = inquiryRepository.findById(create.id!!)

        then(expected.isPresent).isTrue
        then(inquiry.title).isEqualTo(expected.get().title)
        then(inquiry.senderUserId).isEqualTo(expected.get().senderUserId)
        then(inquiry.receiverUserId).isEqualTo(expected.get().receiverUserId)
    }
}
