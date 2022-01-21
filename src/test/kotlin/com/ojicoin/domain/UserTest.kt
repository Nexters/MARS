package com.ojicoin.domain

import com.ojicoin.REPEATED_COUNT
import com.ojicoin.fixture
import com.ojicoin.service.DatabaseFactory
import com.ojicoin.service.DatabaseFactory.dbQuery
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.RepeatedTest
import kotlin.properties.Delegates

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

        assertThat(id, notNullValue())
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

        assertThat(user.nickname, equalTo(createUser.nickname))
        assertThat(user.introduction, equalTo(createUser.introduction))
        assertThat(user.profileUrl, equalTo(createUser.profileUrl))
        assertThat(user.walletAddress, equalTo(createUser.walletAddress))
        assertThat(user.status, equalTo(UserStatus.ACTIVE))
    }
}
