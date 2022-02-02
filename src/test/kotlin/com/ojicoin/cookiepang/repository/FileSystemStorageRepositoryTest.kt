package com.ojicoin.cookiepang.repository

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import kotlin.io.path.Path
import kotlin.io.path.exists

@SpringBootTest
class FileSystemStorageRepositoryTest(
    @Autowired val sut: FileSystemStorageRepository
) {

    val testDirectory: String = "test"

    @Test
    fun store() {
        // given
        val testFileName = "test.png"
        val classPathResource = ClassPathResource("data/jameswebb.png")
        val createdFilePath = Path("$testDirectory/$testFileName")

        val storedPath = sut.store(createdFilePath.toString(), classPathResource.inputStream)

        then(createdFilePath.exists()).isTrue
        then(storedPath).isEqualTo("127.0.0.1:8080/$testDirectory/$testFileName")
    }

    @AfterEach
    fun tearDown() {
        // delete test directory
        val path = Path(testDirectory)
        if (path.exists()) {
            path.toFile().deleteRecursively()
        }
    }
}
