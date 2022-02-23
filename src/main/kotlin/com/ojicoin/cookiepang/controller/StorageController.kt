//package com.ojicoin.cookiepang.controller
//
//import com.ojicoin.cookiepang.service.StorageService
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Profile
//import org.springframework.http.MediaType
//import org.springframework.stereotype.Controller
//import org.springframework.web.servlet.function.RequestPredicates
//import org.springframework.web.servlet.function.RequestPredicates.POST
//import org.springframework.web.servlet.function.RouterFunctions.route
//import org.springframework.web.servlet.function.ServerResponse
//import org.springframework.web.servlet.function.ServerResponse.badRequest
//import org.springframework.web.servlet.function.ServerResponse.created
//import java.net.URI
//
//@Profile("local")
//@Controller
//class StorageController(
//    val storageService: StorageService,
//) {
//
//    @Bean
//    @Deprecated("This api is only for test.")
//    fun getPicture() = route(RequestPredicates.GET("/users/{userId}/{pictureName}")) {
//        val userId = it.pathVariable("userId").toLong()
//        val pictureName = it.pathVariable("pictureName")
//
//        val pictureAsByteArray = storageService.getProfilePicture(userId = userId, pictureName = pictureName)
//        ServerResponse.ok().header("Accept", MediaType.APPLICATION_OCTET_STREAM_VALUE)
//            .body(pictureAsByteArray)
//    }
//
//    @Bean
//    @Deprecated("This api is only for test.")
//    fun addNewPicture() = route(POST("/users/{userId}/pictures")) {
//        val userId = it.pathVariable("userId").toLong()
//        val multiValueMap = it.multipartData()
//
//        val pictureList = multiValueMap["picture"].orEmpty()
//        if (pictureList.size != 1) {
//            return@route badRequest().body("picture must be only one.")
//        }
//
//        val pictureMultiPart = pictureList[0]
//        val createdPicturePath =
//            storageService.saveProfilePicture(userId, pictureMultiPart.submittedFileName, pictureMultiPart.inputStream)
//
//        created(URI.create("")).body(createdPicturePath)
//    }
//}
