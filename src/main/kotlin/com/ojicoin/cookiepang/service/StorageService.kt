package com.ojicoin.cookiepang.service

import com.ojicoin.cookiepang.repository.FileSystemStorageRepository
import org.springframework.stereotype.Service
import java.io.InputStream
import java.nio.file.Files
import java.util.UUID
import kotlin.io.path.Path

@Service
class StorageService(
    val storageRepository: FileSystemStorageRepository
) {

    fun saveProfilePicture(userId: Long, fileName: String, inputStream: InputStream): String {
        val key = "${UUID.randomUUID()}.${getFileExtension(fileName)}"
        return storageRepository.store(key = makeProfilePicturePath(userId, key), inputStream = inputStream)
    }

    // ref: https://github.com/JetBrains/kotlin/blob/master/libraries/stdlib/jvm/src/kotlin/io/files/Utils.kt#L88-L92
    private fun getFileExtension(fileName: String) = fileName.substringAfterLast('.', "")

    // path format "users/{userId}/pictures/{pictureName}"
    private fun makeProfilePicturePath(userId: Long, pictureName: String): String {
        return "users/$userId/pictures/$pictureName"
    }

    // This method is temporary for getting picture in localhost
    fun getProfilePicture(userId: Long, pictureName: String): ByteArray {
        return Files.readAllBytes(Path(makeProfilePicturePath(userId, pictureName)))
    }
}
