package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.AskStatus.PENDING
import com.ojicoin.cookiepang.dto.UpdateAsk
import com.ojicoin.cookiepang.event.AskNotificationEvent
import com.ojicoin.cookiepang.exception.InvalidDomainStatusException
import com.ojicoin.cookiepang.exception.InvalidRequestException
import com.ojicoin.cookiepang.repository.AskRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AskService(
    private val askRepository: AskRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun getBySenderId(userId: Long, pageable: Pageable): List<Ask> {
        return askRepository.findBySenderId(senderId = userId, pageable)
    }

    fun countAsksBySenderId(senderId: Long): Long {
        return askRepository.countBySenderId(senderId = senderId)
    }

    fun getByReceiverId(userId: Long, pageable: Pageable): List<Ask> {
        return askRepository.findByReceiverIdAndStatus(receiverId = userId, status = PENDING, pageable)
    }

    fun countAsksByReceiverId(receiverId: Long): Long {
        return askRepository.countByReceiverIdAndStatus(receiverId = receiverId, status = PENDING)
    }

    fun create(title: String, senderId: Long, receiverId: Long, categoryId: Long): Ask {
        if (senderId == receiverId) {
            throw InvalidRequestException("senderId is same as receiverId.")
                .with("senderId", senderId)
                .with("receiverId", receiverId)
        }

        val savedAsk =
            askRepository.save(
                Ask(
                    title = title,
                    status = PENDING,
                    senderId = senderId,
                    receiverId = receiverId,
                    categoryId = categoryId
                )
            )

        eventPublisher.publishEvent(
            AskNotificationEvent(
                senderUserId = senderId,
                receiverUserId = receiverId,
                cookieTitle = savedAsk.title,
                askId = savedAsk.id!!,
            )
        )

        return savedAsk
    }

    fun modify(id: Long, dto: UpdateAsk): Ask {
        val foundAsk = askRepository.findById(id).orElseThrow()
        if (foundAsk.status != PENDING) {
            throw InvalidDomainStatusException(
                domainType = "ask",
                message = "cannot update ask. This ask status is already changed or is deleted."
            )
                .with("status", foundAsk.status)
        }

        foundAsk.apply(dto)
        return askRepository.save(foundAsk)
    }
}
