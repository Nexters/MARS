package com.ojicoin

import com.ojicoin.domain.CreateViewCount
import com.ojicoin.domain.ViewCounts
import com.ojicoin.domain.toViewCount
import com.ojicoin.service.DatabaseFactory
import com.ojicoin.service.DatabaseFactory.dbQuery
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.RepeatedTest
import kotlin.properties.Delegates
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class DomainTest {

    @RepeatedTest(REPEATED_COUNT)
    fun insertViewCount() {
        runBlocking {
            // given
            var id by Delegates.notNull<Long>()
            val createViewCount = fixture.giveMeOne(CreateViewCount::class.java)

            // when
            dbQuery { id = ViewCounts.insert { createViewCount.apply(it) } get ViewCounts.id }

            assertNotNull(id)
        }
    }

    @RepeatedTest(REPEATED_COUNT)
    fun selectViewCount() {
        runBlocking {
            // given
            val createViewCount = fixture.giveMeOne(CreateViewCount::class.java)
            dbQuery { ViewCounts.insert { createViewCount.apply(it) } }

            // when
            val viewCount = dbQuery { ViewCounts.selectAll().first().toViewCount() }

            assertEquals(createViewCount.userId, viewCount.userId)
            assertEquals(createViewCount.cookieId, viewCount.cookieId)
            assertEquals(createViewCount.count, viewCount.count)
        }
    }

    @AfterEach
    private fun tearDown() {
        transaction { ViewCounts.deleteAll() }
    }

    companion object {
        @BeforeAll
        @JvmStatic
        internal fun setUpAll() {
            DatabaseFactory.connectAndMigrate()
            transaction { SchemaUtils.create(ViewCounts) }
        }

        @AfterAll
        @JvmStatic
        internal fun tearDownAll() {
            transaction { SchemaUtils.drop(ViewCounts) }
        }
    }
}
