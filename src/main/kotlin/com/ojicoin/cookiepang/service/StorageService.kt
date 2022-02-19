package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.repository.StorageRepository
import org.springframework.stereotype.Service
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

@Service
class StorageService(
    private val storageRepository: StorageRepository
) {

    private val userDirectory = "users"

    fun saveProfilePicture(userId: Long, fileName: String, inputStream: InputStream): String {
        val keyWithUserDirectory = "$userDirectory/$userId/${UUID.randomUUID()}.${getFileExtension(fileName)}"

        return storageRepository.store(key = keyWithUserDirectory, inputStream = inputStream)
    }

    // ref: https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/jvm/src/kotlin/io/files/Utils.kt#L88-L92
    private fun getFileExtension(fileName: String) = fileName.substringAfterLast('.', "")

    // This method is temporary for getting picture in local env
    fun getProfilePicture(userId: Long, pictureName: String): ByteArray {
        return Files.readAllBytes(Paths.get("$userDirectory/$userId/$pictureName"))
    }
}
