package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.AskStatus.PENDING
import com.ojicoin.cookiepang.repository.AskRepository
import org.springframework.stereotype.Service

@Service
class AskService(
    private val askRepository: AskRepository
) {

    fun viewAboutSender(userId: Long): List<Ask> {
        return askRepository.findBySenderUserId(senderUserId = userId)
    }

    fun viewAboutReceiver(userId: Long): List<Ask> {
        return askRepository.findByReceiverUserIdAndStatus(receiverUserId = userId, status = PENDING)
    }

    fun create(title: String, senderUserId: Long, receiverUserId: Long): Ask {
        if (senderUserId == receiverUserId) {
            throw IllegalArgumentException("senderUserId is same as receiverUserId. senderUserId=$senderUserId, receiverUserId=$receiverUserId")
        }

        val savedAsk =
            askRepository.save(
                Ask(
                    title = title,
                    status = PENDING,
                    senderUserId = senderUserId,
                    receiverUserId = receiverUserId
                )
            )

        // TODO make notification
        return savedAsk
    }
}
