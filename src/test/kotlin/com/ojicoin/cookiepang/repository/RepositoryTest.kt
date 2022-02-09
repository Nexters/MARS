package com.ojicoin.cookiepang.repository

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Asks
import com.ojicoin.cookiepang.domain.Category
import com.ojicoin.cookiepang.domain.Cookie
<<<<<<< HEAD
import com.ojicoin.cookiepang.domain.Inquiry
=======
import com.ojicoin.cookiepang.domain.CookieCategory
>>>>>>> 2e9e7f6 (Refactoring inquiries to asks)
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.ViewCount
import org.assertj.core.api.BDDAssertions.thenNoException
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

class RepositoryTest(
    @Autowired val cookieRepository: CookieRepository,
<<<<<<< HEAD
    @Autowired val inquiryRepository: InquiryRepository,
=======
    @Autowired val cookieTagRepository: CookieTagRepository,
    @Autowired val askRepository: AskRepository,
>>>>>>> 2e9e7f6 (Refactoring inquiries to asks)
    @Autowired val categoryRepository: CategoryRepository,
    @Autowired val userRepository: UserRepository,
    @Autowired val viewCountRepository: ViewCountRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun insert() {
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .sample()

<<<<<<< HEAD
        val inquiry = fixture.giveMeBuilder<Inquiry>()
=======
        val cookieTag = fixture.giveMeBuilder<CookieCategory>()
            .setNull("id")
            .sample()

        val asks = fixture.giveMeBuilder<Asks>()
>>>>>>> 2e9e7f6 (Refactoring inquiries to asks)
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
<<<<<<< HEAD
            inquiryRepository.save(inquiry)
=======
            cookieTagRepository.save(cookieTag)
            askRepository.save(asks)
>>>>>>> 2e9e7f6 (Refactoring inquiries to asks)
            categoryRepository.save(tag)
            userRepository.save(user)
            viewCountRepository.save(viewCount)
        }
    }
}
