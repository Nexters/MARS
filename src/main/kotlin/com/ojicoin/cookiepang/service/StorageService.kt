package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.repository.StorageRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class StorageService(
    private val storageRepository: StorageRepository
) {
    private val userDirectory = "users"

    fun saveUserPicture(userId: Long, multipartFile: MultipartFile): String {
        val fileName = multipartFile.originalFilename!!
        val inputStream = multipartFile.inputStream
        val keyWithUserDirectory = "$userDirectory/$userId/${UUID.randomUUID()}.${getFileExtension(fileName)}"

        return storageRepository.store(key = keyWithUserDirectory, inputStream = inputStream)
    }

    // ref: https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/jvm/src/kotlin/io/files/Utils.kt#L88-L92
    private fun getFileExtension(fileName: String) = fileName.substringAfterLast('.', "")
}
