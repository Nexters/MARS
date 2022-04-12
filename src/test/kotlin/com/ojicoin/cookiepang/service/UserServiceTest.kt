package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.UserStatus
import com.ojicoin.cookiepang.dto.CreateUser
import com.ojicoin.cookiepang.dto.UpdateUserRequest
import com.ojicoin.cookiepang.exception.DuplicateDomainException
import com.ojicoin.cookiepang.repository.UserRepository
import com.ojicoin.cookiepang.service.UserService.Companion.DEFAULT_USER_BACKGROUND_URL
import com.ojicoin.cookiepang.service.UserService.Companion.DEFAULT_USER_PROFILE_URL
import org.assertj.core.api.BDDAssertions.then
import org.assertj.core.api.BDDAssertions.thenThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.springframework.beans.factory.annotation.Autowired

class UserServiceTest(
    @Autowired val sut: UserService,
    @Autowired val userRepository: UserRepository,
) : SpringContextFixture() {

    @RepeatedTest(REPEAT_COUNT)
    fun create() {
        val createUserDto = fixture.giveMeBuilder(CreateUser::class.java)
            .setNull("profileUrl")
            .setNull("backgroundUrl")
            .sample()

        val actual = sut.create(createUserDto)

        then(createUserDto.walletAddress.lowercase()).isEqualTo(actual.walletAddress)
        then(createUserDto.nickname).isEqualTo(actual.nickname)
        createUserDto.introduction?.also { then(it).isEqualTo(actual.introduction) }

        then(DEFAULT_USER_PROFILE_URL).contains(actual.profileUrl)
        then(DEFAULT_USER_BACKGROUND_URL).contains(actual.backgroundUrl)
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
                status = UserStatus.ACTIVE,
                finishOnboard = false,
            )
        )

        thenThrownBy { sut.create(createUserDto) }.isExactlyInstanceOf(DuplicateDomainException::class.java)
            .hasMessageContaining("There is same nickname user.")
    }

    @RepeatedTest(REPEAT_COUNT)
    fun modify() {
        // given
        val user = fixture.giveMeBuilder(User::class.java)
            .setNull("id")
            .sample()

        val savedUser = userRepository.save(user)
        val updateUserRequestDto =
            fixture.giveMeBuilder(UpdateUserRequest::class.java).setNotNull("introduction").sample()

        // when
        val updatedUser = sut.modify(savedUser.id!!, updateUserRequestDto)

        // then
        updateUserRequestDto.introduction?.also { then(updatedUser.introduction).isEqualTo(it) }
    }

    @RepeatedTest(REPEAT_COUNT)
    fun modifyAboutNullFields() {
        // given
        val user = fixture.giveMeBuilder(User::class.java)
            .setNull("id")
            .sample()
        val savedUser = userRepository.save(user)
        val updateUserRequestDto = fixture.giveMeBuilder(UpdateUserRequest::class.java)
            .setNull("introduction")
            .sample()

        // when
        val updatedUser = sut.modify(savedUser.id!!, updateUserRequestDto)

        then(updatedUser.profileUrl).isEqualTo(savedUser.profileUrl)
        then(updatedUser.backgroundUrl).isEqualTo(savedUser.backgroundUrl)
        then(updatedUser.introduction).isEqualTo(savedUser.introduction)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun modifyThrowNotExistUser() {
        val userId = fixture.giveMeOne(Long::class.java)

        thenThrownBy { sut.modify(userId, UpdateUserRequest(null, null, null)) }
            .isExactlyInstanceOf(NoSuchElementException::class.java)
    }

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }
}
