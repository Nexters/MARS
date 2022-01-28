package com.ojicoin.cookiepang.service

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.UserTag
import com.ojicoin.cookiepang.repository.UserTagRepository
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

internal class UserTagServiceTest(
    @Autowired val userTagRepository: UserTagRepository,
    @Autowired val sut: UserTagService,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun create() {
        // given
        val userTag = fixture.giveMeBuilder<UserTag>()
            .setNull("id")
            .sample()

        sut.create(userTag.userId, listOf(userTag.tagId))
        val expected = userTagRepository.findAllByUserId(userTag.userId)

        then(userTag.userId).isEqualTo(expected[0].userId)
        then(userTag.tagId).isEqualTo(expected[0].tagId)
    }

    // 새로운 user tag를 만드는 경우 기존 등록된 user tag 삭제되는지 확인
    @RepeatedTest(REPEAT_COUNT)
    fun createDeleteAllExistUserTagForTargetUserId() {
        // given
        val userTag = fixture.giveMeBuilder<UserTag>()
            .setNull("id")
            .sample()

        // other tag id
        val newTagId = fixture.giveMeBuilder<Long>()
            .sample()

        userTagRepository.save(userTag)

        sut.create(userTag.userId, listOf(newTagId))
        val expected = userTagRepository.findAllByUserId(userTag.userId)

        expected.forEach { foundUserTag ->
            // create 이전에 만들어진 tag 정보들은 삭제되어야 함.
            run {
                then(userTag.tagId).isNotEqualTo(foundUserTag.tagId)
            }
        }
    }
}
