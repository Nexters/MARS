package com.ojicoin.cookiepang.repository

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieTag
import com.ojicoin.cookiepang.domain.Inquery
import com.ojicoin.cookiepang.domain.Tag
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.ViewCount
import org.assertj.core.api.BDDAssertions.thenNoException
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

class RepositoryTest(
    @Autowired val cookieRepository: CookieRepository,
    @Autowired val cookieTagRepository: CookieTagRepository,
    @Autowired val inqueryRepository: InqueryRepository,
    @Autowired val tagRepository: TagRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val viewCountRepository: ViewCountRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun insert() {
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .sample()

        val cookieTag = fixture.giveMeBuilder<CookieTag>()
            .setNull("id")
            .sample()

        val inquery = fixture.giveMeBuilder<Inquery>()
            .setNull("id")
            .sample()

        val tag = fixture.giveMeBuilder<Tag>()
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
            cookieTagRepository.save(cookieTag)
            inqueryRepository.save(inquery)
            tagRepository.save(tag)
            userRepository.save(user)
            viewCountRepository.save(viewCount)
        }
    }
}
