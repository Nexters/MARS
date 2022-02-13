package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.UserStatus
import com.ojicoin.cookiepang.dto.CreateUser
import com.ojicoin.cookiepang.dto.UpdateUser
import com.ojicoin.cookiepang.repository.UserRepository
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
        val createUserDto =
            fixture.giveMeBuilder(CreateUser::class.java).setNull("profileUrl").setNull("backgroundUrl").sample()

        val actual = sut.create(createUserDto)

        then(createUserDto.walletAddress).isEqualTo(actual.walletAddress)
        then(createUserDto.nickname).isEqualTo(actual.nickname)
        createUserDto.introduction?.also { then(it).isEqualTo(actual.introduction) }

        // TODO test about default url
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

    @RepeatedTest(REPEAT_COUNT)
    fun modify() {
        val user = fixture.giveMeBuilder(User::class.java)
            .setNull("id")
            .sample()

        val savedUser = userRepository.save(user)

        val profilePictureUrl = fixture.giveMeOne(String::class.java)
        val profileBackgroundUrl = fixture.giveMeOne(String::class.java)
        val updateUserDto = fixture.giveMeOne(UpdateUser::class.java)

        val updatedUser = sut.modify(savedUser.id!!, profilePictureUrl, profileBackgroundUrl, updateUserDto)

        profilePictureUrl?.also { then(updatedUser.profileUrl).isEqualTo(it) }
        if (profilePictureUrl == null) then(updatedUser.profileUrl).isEqualTo(savedUser.profileUrl)
        profileBackgroundUrl?.also { then(updatedUser.backgroundUrl).isEqualTo(it) }
        if (profileBackgroundUrl == null) then(updatedUser.backgroundUrl).isEqualTo(savedUser.backgroundUrl)
        updateUserDto.introduction?.also { then(updatedUser.introduction).isEqualTo(it) }
        if (updateUserDto.introduction == null) then(updatedUser.introduction).isEqualTo(savedUser.introduction)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun modifyThrowNotExistUser() {
        val userId = fixture.giveMeOne(Long::class.java)

        thenThrownBy { sut.modify(userId, null, null, UpdateUser(null)) }
            .isExactlyInstanceOf(NoSuchElementException::class.java)
    }

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll()
    }
}
