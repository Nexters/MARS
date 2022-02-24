package com.ojicoin.cookiepang.service

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Category
import com.ojicoin.cookiepang.domain.UserCategory
import com.ojicoin.cookiepang.repository.CategoryRepository
import com.ojicoin.cookiepang.repository.UserCategoryRepository
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

class CategoryServiceTest(
    @Autowired val sut: CategoryService,
    @Autowired val userCategoryRepository: UserCategoryRepository,
    @Autowired val categoryRepository: CategoryRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun getAllByUserId() {
        val category = fixture.giveMeBuilder<Category>()
            .setNull("id")
            .sample()

        val savedCategory = categoryRepository.save(category)

        val userCategory = fixture.giveMeBuilder<UserCategory>()
            .setNull("id")
            .setNotNull("userId")
            .set("categoryId", savedCategory.id)
            .sample()

        val createdUserCategory = userCategoryRepository.save(userCategory)

        val userCategories = sut.getAllCategoriesByUserIdInUserCategory(createdUserCategory.userId)

        then(userCategories.size).isEqualTo(1)
    }
}
