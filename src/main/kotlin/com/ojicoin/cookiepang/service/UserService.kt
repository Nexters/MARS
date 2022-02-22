package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.contract.service.HammerContractService
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.UserStatus.ACTIVE
import com.ojicoin.cookiepang.dto.CreateUser
import com.ojicoin.cookiepang.dto.FinishOnboardView
import com.ojicoin.cookiepang.dto.UpdateUser
import com.ojicoin.cookiepang.exception.DuplicateDomainException
import com.ojicoin.cookiepang.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger

@Service
class UserService(
    val userRepository: UserRepository,
    val hammerContractService: HammerContractService
) {

    @Transactional
    fun create(dto: CreateUser): User {
        // Prevent duplicate user nickname
        userRepository.findByNickname(dto.nickname)
            ?.let {
                throw DuplicateDomainException(domainType = "User", message = "There is same nickname user.")
                    .with("nickname", dto.nickname)
            }

        // TODO set default profile, background url
        val user = User(
            walletAddress = dto.walletAddress,
            nickname = dto.nickname,
            introduction = dto.introduction,
            profileUrl = dto.profileUrl,
            backgroundUrl = dto.backgroundUrl,
            status = ACTIVE,
            finishOnboard = true
        )

        val newUser = userRepository.save(user)

        // TODO: 초반에 이벤트성으로 지급하는 해머 (회원가입 완료시 100 해머를 준다 => 해당 해머는 adminAddress로부터 빠져나가고, 트랜잭션 비용 발생함. 즉, 어드민에 충분한 양의 클레이튼 필요함)
        val hammerAmount: BigInteger = BigInteger.valueOf(1000000000000000000).multiply(BigInteger.valueOf(100))
        hammerContractService.sendHammer(newUser.walletAddress, hammerAmount)
        return newUser
    }

    fun getById(id: Long): User = userRepository.findById(id).orElseThrow()

    fun isFinishOnboard(id: Long): FinishOnboardView = FinishOnboardView(getById(id).finishOnboard)

    fun getByWalletAddress(walletAddress: String): User = userRepository.findByWalletAddress(walletAddress)!!

    fun modify(userId: Long, profilePictureUrl: String?, backgroundPictureUrl: String?, dto: UpdateUser): User {
        val user = userRepository.findById(userId).orElseThrow()

        user.apply(profileUrl = profilePictureUrl, backgroundUrl = backgroundPictureUrl, dto = dto)

        return userRepository.save(user)
    }
}
