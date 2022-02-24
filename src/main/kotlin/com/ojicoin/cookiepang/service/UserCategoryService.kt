package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.UserCategory
import com.ojicoin.cookiepang.dto.CreateUserCategory
import com.ojicoin.cookiepang.repository.UserCategoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserCategoryService(
    private val userCategoryRepository: UserCategoryRepository,
) {

    @Transactional
    fun create(userId: Long, createUserCategory: CreateUserCategory) {
        val userCategoryList = userCategoryRepository.findAllByUserId(userId)
        if (userCategoryList.isNotEmpty()) {
            userCategoryRepository.deleteAllById(userCategoryList.map { userCategory -> userCategory.id })
        }

        val categoryIdList = createUserCategory.categoryIdList
        val newUserCategoryList =
            categoryIdList.map { categoryId -> UserCategory(userId = userId, categoryId = categoryId) }
        userCategoryRepository.saveAll(newUserCategoryList)
    }
}
