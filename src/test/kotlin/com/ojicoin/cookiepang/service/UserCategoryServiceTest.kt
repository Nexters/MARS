package com.ojicoin.cookiepang.service

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.UserCategory
import com.ojicoin.cookiepang.dto.CreateUserCategory
import com.ojicoin.cookiepang.repository.UserCategoryRepository
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

internal class UserCategoryServiceTest(
    @Autowired val sut: UserCategoryService,
    @Autowired val userCategoryRepository: UserCategoryRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun create() {
        // given
        val userCategory = fixture.giveMeBuilder<UserCategory>()
            .setNull("id")
            .sample()

        sut.create(userCategory.userId, CreateUserCategory(categoryIdList = listOf(userCategory.categoryId)))
        val expected = userCategoryRepository.findAllByUserId(userCategory.userId)

        then(userCategory.userId).isEqualTo(expected[0].userId)
        then(userCategory.categoryId).isEqualTo(expected[0].categoryId)
    }

    // 새로운 user category를 만드는 경우 기존 등록된 user category 삭제되는지 확인
    @RepeatedTest(REPEAT_COUNT)
    fun createDeleteAllExistUserCategoryForTargetUserId() {
        // given
        val userCategory = fixture.giveMeBuilder<UserCategory>()
            .setNull("id")
            .sample()

        // other category id
        val newCategoryId = fixture.giveMeOne(Long::class.java)

        userCategoryRepository.save(userCategory)

        sut.create(userCategory.userId, CreateUserCategory(categoryIdList = listOf(newCategoryId)))
        val expected = userCategoryRepository.findAllByUserId(userCategory.userId)

        expected.forEach { foundUserCategory ->
            // create 이전에 만들어진 tag 정보들은 삭제되어야 함.
            run {
                then(userCategory.categoryId).isNotEqualTo(foundUserCategory.categoryId)
            }
        }
    }

    @AfterEach
    fun tearDown() {
        userCategoryRepository.deleteAll()
    }
}
