package com.ojicoin

import com.ojicoin.domain.CreateUser
import com.ojicoin.domain.CreateViewCount
import com.ojicoin.domain.User
import com.ojicoin.domain.UserStatus.ACTIVE
import com.ojicoin.domain.Users
import com.ojicoin.domain.ViewCounts
import com.ojicoin.domain.toUser
import com.ojicoin.domain.toViewCount
import com.ojicoin.service.DatabaseFactory
import com.ojicoin.service.DatabaseFactory.dbQuery
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
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

class UserTest {
    @BeforeEach
    internal fun setUp() {
        DatabaseFactory.connectAndMigrate()
        transaction { SchemaUtils.create(Users) }
    }

    @RepeatedTest(REPEATED_COUNT)
    fun insert() = runBlocking {
        // given
        var id by Delegates.notNull<Long>()
        val createUser = fixture.giveMeOne(CreateUser::class.java)

        // when
        dbQuery { id = Users.insert { createUser.apply(it) } get Users.id }

        assertThat(id, CoreMatchers.notNullValue())
    }

    @RepeatedTest(REPEATED_COUNT)
    fun select() = runBlocking {
        // given
        lateinit var user: User
        var id by Delegates.notNull<Long>()
        val createUser = fixture.giveMeOne(CreateUser::class.java)
        dbQuery { id = Users.insert { createUser.apply(it) } get Users.id }

        // when
        dbQuery { user = Users.select { Users.id eq id }.firstNotNullOf { it.toUser() } }

        assertThat(user.nickname, CoreMatchers.equalTo(createUser.nickname))
        assertThat(user.introduction, CoreMatchers.equalTo(createUser.introduction))
        assertThat(user.profileUrl, CoreMatchers.equalTo(createUser.profileUrl))
        assertThat(user.walletAddress, CoreMatchers.equalTo(createUser.walletAddress))
        assertThat(user.status, CoreMatchers.equalTo(ACTIVE))
    }
}

class ViewCountTest {

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
