package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.UserStatus
import com.ojicoin.cookiepang.dto.CreateUser
import com.ojicoin.cookiepang.repository.UserRepository
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

class UserServiceTest(
    @Autowired val sut: UserService,
    @Autowired val userRepository: UserRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun create() {
        val createUserDto =
            fixture.giveMeBuilder(CreateUser::class.java).setNull("profileUrl").setNull("backgroundUrl").sample()

        val actual = sut.create(createUserDto)

        then(createUserDto.walletAddress).isEqualTo(actual.walletAddress)
        then(createUserDto.nickname).isEqualTo(actual.nickname)
        createUserDto.introduction?.also { then(it).isEqualTo(actual.introduction) }

        // TODO test about default url
        then(actual.profileUrl).isEqualTo("")
        then(actual.backgroundUrl).isEqualTo("")
    }

    @RepeatedTest(REPEAT_COUNT)
    fun createThrowDuplicateNickname() {
        val createUserDto =
            fixture.giveMeBuilder(CreateUser::class.java).setNull("profileUrl").setNull("backgroundUrl").sample()

        userRepository.save(
            User(
                walletAddress = createUserDto.walletAddress,
                nickname = createUserDto.nickname,
                introduction = "",
                profileUrl = "",
                backgroundUrl = "",
                status = UserStatus.ACTIVE
            )
        )
        thenThrownBy { sut.create(createUserDto) }.isExactlyInstanceOf(IllegalArgumentException::class.java)
            .hasMessageContaining("There is same nickname user.")
    }
}
