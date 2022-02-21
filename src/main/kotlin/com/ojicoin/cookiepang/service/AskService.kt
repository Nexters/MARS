package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.AskStatus.PENDING
import com.ojicoin.cookiepang.dto.UpdateAsk
import com.ojicoin.cookiepang.event.AskNotificationEvent
import com.ojicoin.cookiepang.exception.InvalidDomainStatusException
import com.ojicoin.cookiepang.exception.InvalidRequestException
import com.ojicoin.cookiepang.repository.AskRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

@Service
class AskService(
    private val askRepository: AskRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {

    fun viewAboutSender(userId: Long): List<Ask> {
        return askRepository.findBySenderUserId(senderUserId = userId)
    }

    fun viewAboutReceiver(userId: Long): List<Ask> {
        return askRepository.findByReceiverUserIdAndStatus(receiverUserId = userId, status = PENDING)
    }

    fun create(title: String, senderUserId: Long, receiverUserId: Long): Ask {
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
                    receiverUserId = receiverUserId
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

        // TODO make notification about IGNORED, ACCEPTED to senderUserId

        foundAsk.apply(dto)
        return askRepository.save(foundAsk)
    }
}
