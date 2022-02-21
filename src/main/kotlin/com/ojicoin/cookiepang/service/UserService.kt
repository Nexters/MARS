package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.UserStatus.ACTIVE
import com.ojicoin.cookiepang.dto.CreateUser
import com.ojicoin.cookiepang.dto.UpdateUser
import com.ojicoin.cookiepang.exception.DuplicateDomainException
import com.ojicoin.cookiepang.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {

    fun create(dto: CreateUser): User {
        // Prevent duplicate user nickname
        userRepository.findByNickname(dto.nickname)
            ?.let {
                throw DuplicateDomainException(domainType = "User", message = "There is same nickname user.")
                    .with("nickname", dto.nickname)
            }

        // TODO give welcome gift(hammer)

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

    fun getByWalletAddress(walletAddress: String): User = userRepository.findByWalletAddress(walletAddress)!!

    fun modify(userId: Long, profilePictureUrl: String?, backgroundPictureUrl: String?, dto: UpdateUser): User {
        val user = userRepository.findById(userId).orElseThrow()

        user.apply(profileUrl = profilePictureUrl, backgroundUrl = backgroundPictureUrl, dto = dto)

        return userRepository.save(user)
    }
}
