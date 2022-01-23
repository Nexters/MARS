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

		post("/users") {
			val multipartData = call.receiveMultipart()
			val userMap = emptyMap<String, String>().toMutableMap()

			multipartData.forEachPart { part ->
				when (part) {
					is PartData.FormItem -> {
						userMap[part.name ?: ""] = part.value;
					}
					is PartData.FileItem -> {
						val fileName = part.originalFileName as String
						// 업로드된 파일 ByteArray로 가져오는 부분
						var fileBytes = part.streamProvider().readBytes()

						// TODO: thumbnail 생성 서비스 생긴후에 처리 필요
						userMap["profileUrl"] = createThumb(fileName, fileBytes)
					}
				}
			}
			val createUser = CreateUserByMap(userMap)
			dbQuery { Users.insert { createUser.apply(it) } }

			// ObjectMapper 같은거없나?
			val nickname = userMap["nickname"] ?: "";
			// FIXME: 예외 트라이캐치? 공통처리 어떻게?!? 현재는 그냥 exception
			// 닉네임 중복 검사 -> 별도 API로 분리해야될지 검토 필요
			var duplicatedUser = dbQuery { Users.select { Users.nickname eq nickname }.firstNotNullOfOrNull { it?.toUser() } }
			if (duplicatedUser != null) {
				// early return 이 안되서 else 썼는데... response 한다고 함수종료가 안되는데, 함수종료 어떻게하나유?
				call.response.status(HttpStatusCode.Conflict)
			} else {
				dbQuery { Users.insert { createUser.apply(it) } }
				call.response.status(HttpStatusCode.Created)
			}
		}
    }

}

// FIXME: 모듈 빼고 별도로 작업
fun createThumb(fileName: String, fileBytes: ByteArray): String {
	// do something...
	// 파일 생성
	// File("$fileName").writeBytes(fileBytes)
	return "thumbnailurl"
}
