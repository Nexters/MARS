package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.domain.UserCategory
import com.ojicoin.cookiepang.dto.CreateUserCategory
import com.ojicoin.cookiepang.dto.ProblemResponse
import com.ojicoin.cookiepang.service.CategoryService
import com.ojicoin.cookiepang.service.UserCategoryService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class CategoryController(
    private val userCategoryService: UserCategoryService,
    private val categoryService: CategoryService,
) {
    @GetMapping("/users/{userId}/categories")
    fun getUserCategories(@PathVariable userId: String) = userCategoryService.getAllByUserId(userId = userId.toLong())

    @PostMapping("/users/{userId}/categories")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(
        value = [
            ApiResponse(description = "조회 성공", responseCode = "200"),
            ApiResponse(
                description = "잘못된 요청 객체",
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    fun createUserCategories(@PathVariable userId: Long, @RequestBody createUserCategory: CreateUserCategory) {
        userCategoryService.create(userId = userId, createUserCategory = createUserCategory)
    }

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    fun getCategories() = categoryService.getAll()
}
