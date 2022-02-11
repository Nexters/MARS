package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.AskStatus.PENDING
import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.repository.AskRepository
import org.springframework.stereotype.Service

@Service
class AskService(
    private val askRepository: AskRepository
) {

    fun create(title: String, senderUserId: Long, receiverUserId: Long): Ask {
        val savedAsk =
            askRepository.save(Ask(title = title, status = PENDING, senderUserId = senderUserId, receiverUserId = receiverUserId))

        // TODO make notification
        return savedAsk
    }
}
