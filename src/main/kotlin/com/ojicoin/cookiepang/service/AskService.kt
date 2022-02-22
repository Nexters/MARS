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

    fun viewAboutSender(userId: Long, pageable: Pageable): List<Ask> {
        return askRepository.findBySenderUserId(senderUserId = userId, pageable)
    }

    fun viewAboutReceiver(userId: Long, pageable: Pageable): List<Ask> {
        return askRepository.findByReceiverUserIdAndStatus(receiverUserId = userId, status = PENDING, pageable)
    }

    fun countAsksBySenderUserId(senderUserId: Long): Long {
        return askRepository.countBySenderUserId(senderUserId = senderUserId)
    }

    fun countAsksByReceiverUserId(receiverUserId: Long): Long {
        return askRepository.countByReceiverUserIdAndStatus(receiverUserId = receiverUserId, status = PENDING)
    }

    fun create(title: String, senderUserId: Long, receiverUserId: Long, categoryId: Long): Ask {
        if (senderUserId == receiverUserId) {
            throw InvalidRequestException("senderUserId is same as receiverUserId.")
                .with("senderUserId", senderUserId)
                .with("receiverUserId", receiverUserId)
        }

        val savedAsk =
            askRepository.save(
                Ask(
                    title = title,
                    status = PENDING,
                    senderUserId = senderUserId,
                    receiverUserId = receiverUserId,
                    categoryId = categoryId
                )
            )

        eventPublisher.publishEvent(
            AskNotificationEvent(
                senderUserId = senderUserId,
                receiverUserId = receiverUserId,
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
