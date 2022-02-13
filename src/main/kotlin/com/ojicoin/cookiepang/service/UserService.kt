package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.UserStatus.ACTIVE
import com.ojicoin.cookiepang.dto.CreateUser
import com.ojicoin.cookiepang.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {

    fun create(dto: CreateUser): User {
        val findByNickname = userRepository.findByNickname(dto.nickname)
        if (findByNickname.isPresent) {
            throw IllegalArgumentException("There is same nickname user.")
        }

        // TODO set default profile, background url
        val user = User(
            walletAddress = dto.walletAddress,
            nickname = dto.nickname,
            introduction = dto.introduction ?: "",
            profileUrl = dto.profileUrl ?: "",
            backgroundUrl = dto.backgroundUrl ?: "",
            status = ACTIVE
        )

        return userRepository.save(user)
    }

    fun getById(id: Long): User = userRepository.findById(id).orElseThrow()
}
