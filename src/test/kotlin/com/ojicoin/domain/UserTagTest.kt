package com.ojicoin.domain

import com.ojicoin.REPEATED_COUNT
import com.ojicoin.fixture
import com.ojicoin.service.DatabaseFactory
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import kotlin.properties.Delegates


class UserTagTest {
    @BeforeEach
    internal fun setUp() {
        DatabaseFactory.connectAndMigrate()
        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(Tags)
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
