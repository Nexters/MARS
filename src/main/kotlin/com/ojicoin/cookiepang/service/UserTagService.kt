package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.UserTag
import com.ojicoin.cookiepang.repository.UserTagRepository
import org.springframework.stereotype.Service

@Service
class UserTagService(
    private val userTagRepository: UserTagRepository
) {

    fun create(userId: Long, tagIdList: List<Long>) {
        val userTagList = userTagRepository.findAllByUserId(userId)
        if (userTagList.isNotEmpty()) {
            userTagRepository.deleteAllByUserId(userId)
        }

        val newUserTagList = tagIdList.map { tagId -> UserTag(userId = userId, tagId = tagId) }
        userTagRepository.saveAll(newUserTagList)
    }
}
