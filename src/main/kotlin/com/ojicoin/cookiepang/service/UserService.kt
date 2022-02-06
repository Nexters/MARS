package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(val userRepository: UserRepository) {
    fun getById(id: Long): User = userRepository.findById(id).orElseThrow()
}
