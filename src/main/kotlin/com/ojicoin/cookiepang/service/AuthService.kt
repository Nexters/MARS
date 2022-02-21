package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.dto.LoginRequest
import com.ojicoin.cookiepang.dto.LoginResponse
import com.ojicoin.cookiepang.exception.LoginFailedException
import com.ojicoin.cookiepang.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(private val userRepository: UserRepository) {
    fun login(request: LoginRequest): LoginResponse =
        userRepository.findByWalletAddress(walletAddress = request.walletAddress)?.id?.let { LoginResponse(it) }
            ?: throw LoginFailedException("Wallet Address not exists.")
                .with("walletAddress", request.walletAddress)
}
