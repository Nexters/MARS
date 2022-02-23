package com.ojicoin.cookiepang.repository

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import kotlin.io.path.Path

@SpringBootTest
class S3RepositoryTest(
    @Autowired val sut: S3Repository
) {

    val testDirectory: String = "test"

    @Test
    fun store() {
        // given
        val testFileName = "test.png"
        val classPathResource = ClassPathResource("data/jameswebb.png")
        val createdFilePath = Path("$testDirectory/$testFileName")

        sut.store(createdFilePath.toString(), classPathResource.inputStream)
    }
}
