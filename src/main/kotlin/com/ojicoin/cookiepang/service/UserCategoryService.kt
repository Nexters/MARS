package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.UserCategory
import com.ojicoin.cookiepang.repository.UserCategoryRepository
import org.springframework.stereotype.Service

@Service
class UserCategoryService(
    private val userCategoryRepository: UserCategoryRepository,
) {
    fun create(userId: Long, categoryIdList: List<Long>) {
        val userCategoryList = userCategoryRepository.findAllByUserId(userId)
        if (userCategoryList.isNotEmpty()) {
            userCategoryRepository.deleteAllById(userCategoryList.map { userCategory -> userCategory.id })
        }

        val newUserCategoryList =
            categoryIdList.map { categoryId -> UserCategory(userId = userId, categoryId = categoryId) }
        userCategoryRepository.saveAll(newUserCategoryList)
    }
}
