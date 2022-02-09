package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.dto.ViewCategory
import com.ojicoin.cookiepang.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {
    fun getAll() = categoryRepository.findAll().map { ViewCategory(categoryId = it.id!!, name = it.name) }
}
