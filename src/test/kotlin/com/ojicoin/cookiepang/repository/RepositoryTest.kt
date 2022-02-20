package com.ojicoin.cookiepang.repository

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.Category
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.ViewCount
import org.assertj.core.api.BDDAssertions.thenNoException
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

class RepositoryTest(
    @Autowired val cookieRepository: CookieRepository,
    @Autowired val askRepository: AskRepository,
    @Autowired val categoryRepository: CategoryRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val viewCountRepository: ViewCountRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun insert() {
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .sample()

        val ask = fixture.giveMeBuilder<Ask>()
            .setNull("id")
            .sample()

        val tag = fixture.giveMeBuilder<Category>()
            .setNull("id")
            .sample()

        val user = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()

        val viewCount = fixture.giveMeBuilder<ViewCount>()
            .setNull("id")
            .sample()

        thenNoException().isThrownBy {
            cookieRepository.save(cookie)
            askRepository.save(ask)
            categoryRepository.save(tag)
            userRepository.save(user)
            viewCountRepository.save(viewCount)
        }
    }

    @AfterEach
    internal fun tearDown() {
        cookieRepository.deleteAll()
        askRepository.deleteAll()
        categoryRepository.deleteAll()
        userRepository.deleteAll()
        viewCountRepository.deleteAll()
    }
}
