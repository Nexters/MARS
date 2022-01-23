package com.ojicoin.plugins

import com.ojicoin.domain.CreateViewCount
import com.ojicoin.domain.ViewCounts
import com.ojicoin.service.DatabaseFactory.dbQuery
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.insert

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        post("/users/{userId}/cookies/{cookieId}/viewCounts") {
            val cookieId = call.parameters["cookieId"]!!.toLong()
            val userId = call.parameters["userId"]!!.toLong()
            val createViewCount = CreateViewCount(userId = userId, cookieId = cookieId, count = 1L)
            dbQuery { ViewCounts.insert { createViewCount.apply(it) } }
            call.response.status(HttpStatusCode.Created)
        }
    }
}
