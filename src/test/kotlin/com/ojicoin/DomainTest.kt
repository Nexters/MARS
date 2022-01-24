package com.ojicoin

import com.ojicoin.domain.CreateUserTag
import com.ojicoin.domain.CreateViewCount
import com.ojicoin.domain.UserTag
import com.ojicoin.domain.UserTags
import com.ojicoin.domain.ViewCounts
import com.ojicoin.domain.toUserTag
import com.ojicoin.domain.toViewCount
import com.ojicoin.service.DatabaseFactory
import com.ojicoin.service.DatabaseFactory.dbQuery
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
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
