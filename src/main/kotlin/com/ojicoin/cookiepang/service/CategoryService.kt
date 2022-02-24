package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.Category
import com.ojicoin.cookiepang.repository.CategoryRepository
import com.ojicoin.cookiepang.repository.UserCategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val userCategoryRepository: UserCategoryRepository,
) {
    fun getAll() = categoryRepository.findAll().map { it.toCategoryView() }

    fun getById(id: Long) = categoryRepository.findById(id).orElseThrow()

    fun getByName(name: String) = categoryRepository.findByName(name) ?: throw NoSuchElementException()

    fun getAllCategoriesByUserIdInUserCategory(userId: Long): List<Category> {
        val findAllByUserId = userCategoryRepository.findAllByUserId(userId = userId)

        return findAllByUserId.map { categoryRepository.findById(it.categoryId).orElseThrow() }.toList()
    }
}
