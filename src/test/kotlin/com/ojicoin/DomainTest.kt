package com.ojicoin

import com.ojicoin.domain.*
import com.ojicoin.domain.Tags
import com.ojicoin.service.DatabaseFactory
import com.ojicoin.service.DatabaseFactory.dbQuery
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
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

class UserTagTest {
    @BeforeEach
    internal fun setUp() {
        DatabaseFactory.connectAndMigrate()
        transaction {
            SchemaUtils.create(UserTags)
        }
    }

    @RepeatedTest(REPEATED_COUNT)
    fun insert() = runBlocking {
        // given
        var userId by Delegates.notNull<Long>()
        val createUserTag = fixture.giveMeOne(CreateUserTag::class.java)

        // when
        DatabaseFactory.dbQuery { userId = UserTags.insert { createUserTag.apply(it) } get UserTags.userId }

        MatcherAssert.assertThat(userId, CoreMatchers.notNullValue())
    }

    @RepeatedTest(REPEATED_COUNT)
    fun select() = runBlocking {
        // given
        lateinit var userTag: UserTag
        var userId by Delegates.notNull<Long>()
        val createUserTag = fixture.giveMeOne(CreateUserTag::class.java)
        DatabaseFactory.dbQuery { userId = UserTags.insert { createUserTag.apply(it) } get UserTags.userId }

        // when
        DatabaseFactory.dbQuery {
            userTag = UserTags.select { UserTags.userId eq userId }.firstNotNullOf { it.toUserTag() }
        }

        MatcherAssert.assertThat(userTag.tagId, CoreMatchers.equalTo(createUserTag.tagId))
        MatcherAssert.assertThat(userTag.userId, CoreMatchers.equalTo(createUserTag.userId))
    }
}
