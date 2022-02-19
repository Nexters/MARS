package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.controller.UserExistException
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.UserStatus.ACTIVE
import com.ojicoin.cookiepang.dto.CreateUser
import com.ojicoin.cookiepang.dto.UpdateUser
import com.ojicoin.cookiepang.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {

    fun create(dto: CreateUser): User {
        // Prevent duplicate user nickname
        userRepository.findByNickname(dto.nickname)
            ?.let { throw IllegalArgumentException("There is same nickname user.") }

        // TODO set default profile, background url
        val user = User(
            walletAddress = dto.walletAddress,
            nickname = dto.nickname,
            introduction = dto.introduction,
            profileUrl = dto.profileUrl,
            backgroundUrl = dto.backgroundUrl,
            status = ACTIVE
        )

        return userRepository.save(user)
    }

    fun getById(id: Long): User = userRepository.findById(id).orElseThrow()

    fun modify(userId: Long, profilePictureUrl: String?, backgroundPictureUrl: String?, dto: UpdateUser): User {
        val user = userRepository.findById(userId).orElseThrow()

        user.apply(profileUrl = profilePictureUrl, backgroundUrl = backgroundPictureUrl, dto = dto)

        return userRepository.save(user)
    }

    fun checkDuplicateUser(walletAddress: String) {
        userRepository.findByWalletAddress(walletAddress = walletAddress)
            ?.let { throw UserExistException("There is user that have this wallerAddress. wallerAddress=$walletAddress") }
    }
}
