package com.ojicoin

import com.ojicoin.plugins.configureRouting
import com.ojicoin.plugins.configureSerialization
import com.ojicoin.service.DatabaseFactory
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
        DatabaseFactory.connectAndMigrate()
    }.start(wait = true)
}
