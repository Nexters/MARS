package com.ojicoin.cookiepang.repository

import com.ojicoin.cookiepang.config.LocalStorageProperties
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.io.InputStream
import java.nio.file.Files
import kotlin.io.path.Path

@Profile("local")
@Repository
class FileSystemStorageRepository(
    val localStorageProperties: LocalStorageProperties,
) : StorageRepository {

    // This key must have file extension. ex) image.jpg, image.png
    override fun store(key: String, inputStream: InputStream): String {
        val path = Path(key)
        if (Files.notExists(path.parent)) {
            Files.createDirectories(path.parent)
        }

        Files.copy(inputStream, path)

        return makeHostFilePath(key)
    }

    // hostFilePath format: domain(with port number)/objectPath
    // ex) localhost:8080/users/1/pictures/234u2341.png, cookiepang.com:8080/users/2/pictures/2342929371.png
    private fun makeHostFilePath(key: String): String {
        return "${localStorageProperties.domain}/$key"
    }
}
