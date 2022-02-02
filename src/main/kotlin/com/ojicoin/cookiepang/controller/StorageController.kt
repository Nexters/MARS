package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.service.StorageService
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.function.RequestPredicates.POST
import org.springframework.web.servlet.function.RouterFunctions.route
import org.springframework.web.servlet.function.ServerResponse.badRequest
import org.springframework.web.servlet.function.ServerResponse.created
import java.net.URI

@Controller
class StorageController(
    val storageService: StorageService
) {

    @Deprecated("This api is only for test.")
    @Bean
    fun addNewPicture() = route(POST("/users/{userId}/pictures")) {
        val userId = it.pathVariable("userId").toLong()
        val multiValueMap = it.multipartData()

        val pictureList = multiValueMap["picture"].orEmpty()
        if (pictureList.size != 1) {
            return@route badRequest().body("picture must be only one.")
        }

        val pictureMultiPart = pictureList[0]
        val createdPicturePath =
            storageService.saveProfilePicture(userId, pictureMultiPart.submittedFileName, pictureMultiPart.inputStream)

        created(URI.create("")).body(createdPicturePath)
    }
}
