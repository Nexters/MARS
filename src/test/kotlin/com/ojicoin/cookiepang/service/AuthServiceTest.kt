package com.ojicoin.cookiepang.service

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.navercorp.fixturemonkey.kotlin.giveMeOne
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.dto.LoginRequest
import com.ojicoin.cookiepang.exception.ForbiddenRequestException
import com.ojicoin.cookiepang.repository.UserRepository
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

class AuthServiceTest(
    @Autowired val sut: AuthService,
    @Autowired val userRepository: UserRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun login() {
        val user = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val loginRequest = fixture.giveMeBuilder<LoginRequest>()
            .set("walletAddress", user.walletAddress)
            .sample()
        userRepository.save(user)

        val actual = sut.login(loginRequest)

        then(actual.userId).isEqualTo(user.id)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun loginWhenWrongWalletAddressThrows() {
        val loginRequest = fixture.giveMeOne<LoginRequest>()

        thenThrownBy { sut.login(loginRequest) }
            .isExactlyInstanceOf(ForbiddenRequestException::class.java)
    }

    @AfterEach
    internal fun tearDown() {
        userRepository.deleteAll()
    }
}
