package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.dto.CreateUser
import com.ojicoin.cookiepang.dto.ProblemResponse
import com.ojicoin.cookiepang.dto.UpdateUser
import com.ojicoin.cookiepang.service.StorageService
import com.ojicoin.cookiepang.service.UserService
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class UserController(
    private val userService: UserService,
    private val storageService: StorageService,
) {
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(
        value = [
            ApiResponse(description = "생성 성공", responseCode = "200"),
            ApiResponse(
                description = "중복",
                responseCode = "407",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    fun createUser(@RequestBody createUser: CreateUser): User = userService.create(createUser)

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(
        value = [
            ApiResponse(description = "조회 성공", responseCode = "200"),
            ApiResponse(
                description = "디비에 존재하지 않음",
                responseCode = "400",
                content = [Content(schema = Schema(implementation = ProblemResponse::class))]
            )
        ]
    )
    fun getUser(@PathVariable("userId") userId: Long) = userService.getById(id = userId)

    @PutMapping("/users/{userId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateUser(
        @PathVariable userId: Long,
        @RequestParam("introduction", required = false) introduction: String?,
        @RequestPart("profilePicture", required = false) profilePicture: MultipartFile?,
        @RequestPart("backgroundPicture", required = false) backgroundPicture: MultipartFile?,
    ): User {
        val profilePictureUrl = uploadPictureAndGetPictureUrlIfExistPicture(profilePicture, userId)

        // upload background picture
        val backgroundPictureUrl =
            uploadPictureAndGetPictureUrlIfExistPicture(backgroundPicture, userId)

        val dto = UpdateUser(introduction = introduction)

        return userService.modify(
            userId = userId,
            profilePictureUrl = profilePictureUrl,
            backgroundPictureUrl = backgroundPictureUrl,
            dto = dto
        )
    }

    private fun uploadPictureAndGetPictureUrlIfExistPicture(
        multipartFile: MultipartFile?,
        userId: Long,
    ): String? = if (multipartFile == null) {
        null
    } else {
        storageService.saveProfilePicture(userId, multipartFile.originalFilename!!, multipartFile.inputStream)
    }
}
