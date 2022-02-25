package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.dto.LoginRequest
import com.ojicoin.cookiepang.dto.LoginResponse
import com.ojicoin.cookiepang.exception.ForbiddenRequestException
import com.ojicoin.cookiepang.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(private val userRepository: UserRepository) {
    fun login(request: LoginRequest): LoginResponse =
        userRepository.findByWalletAddress(walletAddress = request.walletAddress.lowercase())?.id?.let {
            LoginResponse(
                it
            )
        }
            ?: throw ForbiddenRequestException("Wallet Address not exists.")
                .with("walletAddress", request.walletAddress)
}
