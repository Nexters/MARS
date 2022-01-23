package com.ojicoin

import com.ojicoin.domain.CreateViewCount
import com.ojicoin.domain.ViewCounts
import com.ojicoin.plugins.configureRouting
import com.ojicoin.plugins.configureSerialization
import com.ojicoin.service.DatabaseFactory
import com.ojicoin.service.DatabaseFactory.dbQuery
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.TestApplication
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import kotlin.test.assertEquals

class RoutingTest {
    @RepeatedTest(REPEATED_COUNT)
    fun createViewCount() = runBlocking {
        // given
        val createViewCount = fixture.giveMeOne(CreateViewCount::class.java)

        // when
        val response =
            context.client.post("/users/${createViewCount.userId}/cookies/${createViewCount.cookieId}/viewCounts")

        // then
        assertEquals(HttpStatusCode.Created, response.status)
    }

    @AfterEach
    private fun tearDown() {
        runBlocking { dbQuery { ViewCounts.deleteAll() } }
    }

    companion object {
        private val context = TestApplication {
            application {
                configureRouting()
                configureSerialization()
                DatabaseFactory.connectAndMigrate()
                transaction { SchemaUtils.create(ViewCounts) }
            }
        }

        @AfterAll
        @JvmStatic
        internal fun tearDownAll() {
            context.stop()
        }
    }
}
