package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.AskStatus.PENDING
import com.ojicoin.cookiepang.domain.Asks
import com.ojicoin.cookiepang.repository.AskRepository
import org.springframework.stereotype.Service

@Service
class AskService(
    private val askRepository: AskRepository
) {

    fun create(title: String, senderUserId: Long, receiverUserId: Long): Asks {
        val savedAsk =
            askRepository.save(Asks(title = title, status = PENDING, senderUserId = senderUserId, receiverUserId = receiverUserId))

        // TODO make notification
        return savedAsk
    }
}
