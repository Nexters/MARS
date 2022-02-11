package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.AskStatus.PENDING
import com.ojicoin.cookiepang.dto.UpdateAsk
import com.ojicoin.cookiepang.repository.AskRepository
import org.springframework.stereotype.Service

@Service
class AskService(
    private val askRepository: AskRepository
) {

    fun create(title: String, senderUserId: Long, receiverUserId: Long): Ask {
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

    fun modify(id: Long, dto: UpdateAsk): Ask {
        val foundAsk = askRepository.findById(id).orElseThrow()
        if (foundAsk.status != PENDING) {
            throw IllegalArgumentException("cannot update ask. This ask status is already changed or is deleted. status=${foundAsk.status}")
        }

        // TODO make notification about IGNORED, ACCEPTED to senderUserId

        foundAsk.apply(dto)
        return askRepository.save(foundAsk)
    }
}
