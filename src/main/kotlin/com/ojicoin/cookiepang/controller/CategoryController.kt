package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.service.CategoryService
import com.ojicoin.cookiepang.service.UserCategoryService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class CategoryController(
    private val userCategoryService: UserCategoryService,
    private val categoryService: CategoryService,
) {
    @PostMapping("/users/{userId}/categories")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUserCategories(@PathVariable userId: Long, createUserCategory: CreateUserCategory) {
        userCategoryService.create(userId, createUserCategory.categoryIdList)
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    fun getCategories() = categoryService.getAll()
}

enum class GetUserCookieTarget {
    COLLECTED,
    COOKIES,
}

data class CreateUserCategory(
    val categoryIdList: List<Long>,
)
