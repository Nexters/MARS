package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.Inquiry
import com.ojicoin.cookiepang.repository.InquiryRepository
import org.springframework.stereotype.Service

@Service
class InquiryService(
    private val inquiryRepository: InquiryRepository
) {

    fun create(title: String, senderUserId: Long, receiverUserId: Long): Inquiry {
        val savedInquiry =
            inquiryRepository.save(Inquiry(title = title, senderUserId = senderUserId, receiverUserId = receiverUserId))

        // TODO make notification
        return savedInquiry
    }

}
