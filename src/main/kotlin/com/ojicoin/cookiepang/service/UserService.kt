package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.contract.service.HammerContractService
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.domain.UserStatus.ACTIVE
import com.ojicoin.cookiepang.dto.CreateUser
import com.ojicoin.cookiepang.dto.FinishOnboardView
import com.ojicoin.cookiepang.dto.UpdateUser
import com.ojicoin.cookiepang.dto.UpdateUserRequest
import com.ojicoin.cookiepang.dto.UserView
import com.ojicoin.cookiepang.exception.DuplicateDomainException
import com.ojicoin.cookiepang.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigInteger
import java.util.concurrent.ThreadLocalRandom

@Service
class UserService(
    val userRepository: UserRepository,
    val hammerContractService: HammerContractService,
    private val storageService: StorageService,
) {
    private val HAMMER_DEFAULT_DIGIT: BigInteger = BigInteger.valueOf(1000000000000000000)
    private val HAMMER_SEND_AMOUNT: BigInteger = BigInteger.valueOf(100)

    @Transactional
    fun create(dto: CreateUser): UserView {
        // Prevent duplicate user nickname
        userRepository.findByNickname(dto.nickname)
            ?.let {
                throw DuplicateDomainException(domainType = "User", message = "There is same nickname user.")
                    .with("nickname", dto.nickname)
            }

        val randomNumber = ThreadLocalRandom.current().nextInt(0, 1)

        val user = User(
            walletAddress = dto.walletAddress.lowercase(),
            nickname = dto.nickname,
            introduction = dto.introduction,
            profileUrl = dto.profileUrl ?: DEFAULT_USER_PROFILE_URL[randomNumber],
            backgroundUrl = dto.backgroundUrl ?: DEFAULT_USER_BACKGROUND_URL[randomNumber],
            status = ACTIVE,
            finishOnboard = false,
            deviceToken = dto.deviceToken ?: "",
        )

        val newUser = userRepository.save(user)

        // TODO: 초반에 이벤트성으로 지급하는 해머 (회원가입 완료시 100 해머를 준다 => 해당 해머는 adminAddress로부터 빠져나가고, 트랜잭션 비용 발생함. 즉, 어드민에 충분한 양의 클레이튼 필요함)
        val hammerAmount: BigInteger = HAMMER_SEND_AMOUNT.multiply(HAMMER_DEFAULT_DIGIT)
        hammerContractService.sendHammer(newUser.walletAddress, hammerAmount)

        return convertToDto(newUser)
    }

    fun getById(id: Long): User {
        return userRepository.findById(id).orElseThrow()
    }

    fun getUserViewById(id: Long): UserView {
        val user = userRepository.findById(id).orElseThrow()

        return convertToDto(user)
    }

    fun isFinishOnboard(id: Long): FinishOnboardView = FinishOnboardView(getById(id).finishOnboard)

    fun getByWalletAddress(walletAddress: String): User = userRepository.findByWalletAddress(walletAddress)!!

    fun modify(userId: Long, updateUserRequest: UpdateUserRequest): UserView {
        val user = userRepository.findById(userId).orElseThrow()

        val updateProfilePictureUrl: String? = if (updateUserRequest.profilePicture != null) {
            storageService.saveUserPicture(userId = userId, multipartFile = updateUserRequest.profilePicture)
        } else {
            null
        }

        val updateBackgroundPictureUrl: String? = if (updateUserRequest.backgroundPicture != null) {
            storageService.saveUserPicture(userId = userId, multipartFile = updateUserRequest.backgroundPicture)
        } else {
            null
        }

        user.apply(
            updateUser = UpdateUser(
                introduction = updateUserRequest.introduction,
                deviceToken = updateUserRequest.deviceToken,
                profilePictureUrl = updateProfilePictureUrl,
                backgroundPictureUrl = updateBackgroundPictureUrl
            )
        )

        val updatedUser = userRepository.save(user)
        return convertToDto(updatedUser)
    }

    private fun convertToDto(entity: User): UserView {
        return UserView(
            id = entity.id!!,
            walletAddress = entity.walletAddress,
            nickname = entity.nickname,
            introduction = entity.introduction ?: "",
            profileUrl = entity.profileUrl ?: "",
            backgroundUrl = entity.backgroundUrl ?: "",
            status = entity.status,
            finishOnboard = entity.finishOnboard,
        )
    }

    companion object {
        val DEFAULT_USER_PROFILE_URL: List<String> = listOf(
            "https://cdn.cookiepang.site/pictures/users/default/default-profile-1.png",
            "https://cdn.cookiepang.site/pictures/users/default/default-profile-2.png"
        )
        val DEFAULT_USER_BACKGROUND_URL: List<String> = listOf(
            "https://cdn.cookiepang.site/pictures/users/default/default-background-1.png",
            "https://cdn.cookiepang.site/pictures/users/default/default-background-2.png"
        )
    }
}
