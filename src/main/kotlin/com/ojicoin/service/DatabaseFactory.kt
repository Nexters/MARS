package com.ojicoin.service

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object DatabaseFactory {
    fun connectAndMigrate() {
        val hikariPool = HikariDataSource(
            HikariConfig().apply {
                driverClassName = "org.h2.Driver"
                jdbcUrl = "jdbc:h2:mem:test"
                maximumPoolSize = 3
                isAutoCommit = false
                transactionIsolation = "TRANSACTION_REPEATABLE_READ"
                validate()
            }
        )
        Database.connect(hikariPool)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction { block() }
}
