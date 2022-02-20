package com.ojicoin.cookiepang.controller

import com.ojicoin.cookiepang.controller.GetAskTarget.RECEIVER
import com.ojicoin.cookiepang.controller.GetAskTarget.SENDER
import com.ojicoin.cookiepang.controller.GetUserCookieTarget.COLLECTED
import com.ojicoin.cookiepang.controller.GetUserCookieTarget.COOKIES
import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.Notification
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.dto.CategoryView
import com.ojicoin.cookiepang.dto.CreateAsk
import com.ojicoin.cookiepang.dto.CreateCookie
import com.ojicoin.cookiepang.dto.CreateUser
import com.ojicoin.cookiepang.dto.GetCookiesResponse
import com.ojicoin.cookiepang.dto.UpdateAsk
import com.ojicoin.cookiepang.dto.UpdateCookie
import com.ojicoin.cookiepang.dto.UpdateUser
import com.ojicoin.cookiepang.service.AskService
import com.ojicoin.cookiepang.service.CategoryService
import com.ojicoin.cookiepang.service.CookieService
import com.ojicoin.cookiepang.service.NotificationService
import com.ojicoin.cookiepang.service.StorageService
import com.ojicoin.cookiepang.service.UserCategoryService
import com.ojicoin.cookiepang.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springdoc.core.annotations.RouterOperation
import org.springdoc.core.annotations.RouterOperations
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.util.MultiValueMap
import org.springframework.web.servlet.function.RequestPredicates.DELETE
import org.springframework.web.servlet.function.RequestPredicates.GET
import org.springframework.web.servlet.function.RequestPredicates.POST
import org.springframework.web.servlet.function.RequestPredicates.PUT
import org.springframework.web.servlet.function.RouterFunctions.route
import org.springframework.web.servlet.function.ServerResponse.created
import org.springframework.web.servlet.function.ServerResponse.noContent
import org.springframework.web.servlet.function.ServerResponse.ok
import org.springframework.web.servlet.function.body
import java.net.URI
import javax.servlet.http.Part

@Controller
class ApiController(
    private val askService: AskService,
    private val userService: UserService,
    private val userCategoryService: UserCategoryService,
    private val cookieService: CookieService,
    private val categoryService: CategoryService,
    private val storageService: StorageService,
    private val notificationService: NotificationService,
) {

    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/asks",
            consumes = ["application/json"],
            operation = Operation(
                operationId = "createAsks",
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = CreateAsk::class))],
                ),
                responses = [
                    ApiResponse(
                        responseCode = "201",
                        content = [Content(schema = Schema(implementation = Ask::class))],
                    )
                ]
            ),
        ),
        RouterOperation(
            path = "/users/{userId}/categories",
            operation = Operation(
                operationId = "createUserTags",
                parameters = [
                    Parameter(name = "userId", `in` = ParameterIn.PATH),
                ],
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = UserCategoryCreateDto::class))]
                ),
                responses = [ApiResponse(responseCode = "200")]
            ),
        ),
        RouterOperation(
            path = "/cookies",
            consumes = ["application/json"],
            operation = Operation(
                operationId = "createCookies",
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = CreateCookie::class))]
                ),
                responses = [
                    ApiResponse(
                        responseCode = "201",
                        content = [Content(schema = Schema(implementation = Cookie::class))]
                    )
                ]
            ),
        ),
        RouterOperation(
            path = "/users",
            consumes = ["application/json"],
            operation = Operation(
                operationId = "createUsers",
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = CreateUser::class))]
                ),
                responses = [
                    ApiResponse(
                        responseCode = "201",
                        content = [Content(schema = Schema(implementation = User::class))]
                    )
                ]
            ),
        )
    )
    fun create() = route(POST("/asks")) {
        val createAskDto = it.body<CreateAsk>()

        val savedAsk = askService.create(createAskDto.title, createAskDto.senderUserId, createAskDto.receiverUserId)

        created(URI.create("")).body(savedAsk)
    }.andRoute(POST("/users/{userId}/categories")) {
        // create user interested tags
        val userId = it.pathVariable("userId").toLong()
        val userCategoryCreateDto = it.body<UserCategoryCreateDto>()

        userCategoryService.create(userId, userCategoryCreateDto.categoryIdList)

        // TODO create certain uri path about created resource
        created(URI.create("")).build()
    }.andRoute(POST("/cookies")) {
        val dto = it.body(CreateCookie::class.java)
        val cookie = cookieService.create(dto)
        created(URI.create("/users/${dto.ownedUserId}/cookies/${cookie.id}/detail")).body(cookie)
    }.andRoute(POST("/users")) {
        val dto = it.body(CreateUser::class.java)
        val savedUser = userService.create(dto = dto)

        // TODO create certain uri path about created resource
        created(URI.create("")).body(savedUser)
    }

    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/categories",
            operation = Operation(
                operationId = "getAllCategories",
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        content = [
                            Content(
                                mediaType = "application/json",
                                array = ArraySchema(schema = Schema(implementation = CategoryView::class))
                            )
                        ]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/{userId}",
            operation = Operation(
                operationId = "getUser",
                method = "GET",
                tags = ["user"],
                parameters = [Parameter(name = "userId", `in` = ParameterIn.PATH)],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        content = [
                            Content(
                                mediaType = "application/json",
                                schema = Schema(implementation = User::class)
                            )
                        ]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/notifications/users/{userId}",
            operation = Operation(
                operationId = "getNotifications",
                parameters = [
                    Parameter(
                        name = "userId",
                        content = [Content(schema = Schema(implementation = Long::class))],
                        `in` = ParameterIn.PATH
                    ),
                    Parameter(
                        name = "page",
                        content = [Content(schema = Schema(implementation = Int::class, defaultValue = "0"))],
                        `in` = ParameterIn.QUERY
                    ),
                    Parameter(
                        name = "size",
                        content = [Content(schema = Schema(implementation = Int::class, defaultValue = "5"))],
                        `in` = ParameterIn.QUERY
                    ),
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        content = [
                            Content(
                                mediaType = "application/json",
                                array = ArraySchema(schema = Schema(implementation = Notification::class))
                            )
                        ]
                    )
                ]
            )
        ),
        RouterOperation(
            path = "/users/{userId}/cookies",
            operation = Operation(
                operationId = "getUserCookies",
                method = "GET",
                tags = ["cookie"],
                parameters = [
                    Parameter(name = "userId", `in` = ParameterIn.PATH),
                    Parameter(
                        name = "page",
                        content = [Content(schema = Schema(implementation = Int::class, defaultValue = "0"))],
                        `in` = ParameterIn.QUERY
                    ),
                    Parameter(
                        name = "size",
                        content = [Content(schema = Schema(implementation = Int::class, defaultValue = "3"))],
                        `in` = ParameterIn.QUERY
                    ),
                    Parameter(
                        name = "target",
                        content = [Content(schema = Schema(implementation = GetUserCookieTarget::class))],
                        `in` = ParameterIn.QUERY
                    ),
                ],
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        content = [
                            Content(
                                mediaType = "application/json",
                                array = ArraySchema(schema = Schema(implementation = GetCookiesResponse::class))
                            )
                        ]
                    )
                ]
            )
        )
    )
    fun view() = route(GET("/categories")) {
        ok().body(categoryService.getAll())
    }.andRoute(GET("/asks/users/{userId}")) {
        val userId = it.pathVariable("userId").toLong()
        // target: sender, receiver
        val target = it.param("target").orElseThrow()

        val asks = when (GetAskTarget.valueOf(target.uppercase())) {
            SENDER -> askService.viewAboutSender(userId = userId)

            RECEIVER -> askService.viewAboutReceiver(userId = userId)
        }

        ok().body(asks)
    }.andRoute(GET("/users/{userId}")) {
        val userId = it.pathVariable("userId").toLong()

        ok().body(userService.getById(id = userId))
    }.andRoute(GET("/notifications/users/{userId})")) {
        val receiverUserId = it.pathVariable("userId").toLong()

        val page = it.param("page").map { page -> page.toInt() }.orElse(0)
        val size = it.param("size").map { size -> size.toInt() }.orElse(5)

        ok().body(notificationService.get(receiverUserId = receiverUserId, page = page, size = size))
    }.andRoute(GET("/users/{userId}/cookies")) {
        val userId = it.pathVariable("userId").toLong()

        val page = it.param("page").map { page -> page.toInt() }.orElse(0)
        val size = it.param("size").map { size -> size.toInt() }.orElse(5)

        // TODO get viewUserId
        val viewUserId = 1

        val target = it.param("target").orElseThrow()
        val cookiesResponse = when (GetUserCookieTarget.valueOf(target.uppercase())) {
            COLLECTED -> cookieService.getOwnedCookies(
                userId = userId,
                viewUserId = viewUserId.toLong(),
                page = page,
                size = size
            )

            COOKIES -> cookieService.getAuthorCookies(
                userId = userId,
                viewUserId = viewUserId.toLong(),
                page = page,
                size = size
            )
        }

        ok().body(cookiesResponse)
    }

    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/cookies/{cookieId}",
            operation = Operation(
                operationId = "deleteCookies",
                parameters = [Parameter(name = "cookieId", `in` = ParameterIn.PATH)],
                responses = [ApiResponse(responseCode = "204")]
            ),
        ),
    )
    fun delete() = route(DELETE("/cookies/{cookieId}")) {
        val cookieId = it.pathVariable("cookieId").toLong()
        cookieService.delete(cookieId = cookieId)
        noContent().build()
    }

    @Bean
    @RouterOperations(
        RouterOperation(
            path = "/cookies/{cookieId}",
            consumes = ["application/json"],
            operation = Operation(
                operationId = "updateCookie",
                parameters = [Parameter(name = "cookieId", `in` = ParameterIn.PATH)],
                requestBody = RequestBody(
                    required = true,
                    content = [Content(schema = Schema(implementation = UpdateCookie::class))]
                ),
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        content = [Content(schema = Schema(implementation = Cookie::class))]
                    )
                ]
            ),
        ),
        RouterOperation(
            path = "/users/{userId}",
            consumes = ["multipart/form-data"],
            produces = ["application/json"],
            operation = Operation(
                operationId = "updateUser",
                tags = ["User"],
                method = "PUT",
                parameters = [Parameter(name = "userId", `in` = ParameterIn.PATH)],
                requestBody = RequestBody(
                    required = true,
                    content = [
                        Content(schema = Schema(name = "updateUser", implementation = UpdateUser::class)),
                        // TODO add profilePicture, backgroundPicture as multipart/form-data
                    ],
                ),
                responses = [
                    ApiResponse(
                        responseCode = "200",
                        content = [Content(schema = Schema(implementation = User::class))]
                    )
                ]
            )
        )
    )
    fun modify() = route(PUT("/cookies/{cookieId}")) {
        val cookieId = it.pathVariable("cookieId").toLong()
        val dto = it.body(UpdateCookie::class.java)
        val updated = cookieService.modify(cookieId = cookieId, updateCookie = dto)
        ok().body(updated)
    }.andRoute(PUT("/users/{userId}")) {
        val userId = it.pathVariable("userId").toLong()

        val multiPartMap = it.multipartData()
        // upload profile picture
        val profilePictureUrl = uploadPictureAndGetPictureUrlIfExistPicture(multiPartMap, "profilePicture", userId)

        // upload background picture
        val backgroundPictureUrl =
            uploadPictureAndGetPictureUrlIfExistPicture(multiPartMap, "backgroundPicture", userId)

        val dto = UpdateUser(introduction = it.param("introduction").orElse(null))

        val updatedUser = userService.modify(
            userId = userId,
            profilePictureUrl = profilePictureUrl,
            backgroundPictureUrl = backgroundPictureUrl,
            dto = dto
        )

        ok().body(updatedUser)
    }.andRoute(PUT("/asks/{askId}")) {
        val askId = it.pathVariable("askId").toLong()
        val dto = it.body(UpdateAsk::class.java)

        val modifiedAsk = askService.modify(id = askId, dto = dto)

        ok().body(modifiedAsk)
    }

    fun uploadPictureAndGetPictureUrlIfExistPicture(
        multipartData: MultiValueMap<String, Part>,
        pictureKey: String,
        userId: Long,
    ): String? {
        if (multipartData[pictureKey].isNullOrEmpty()) {
            return null
        }

        val picture = multipartData[pictureKey]!![0]
        return storageService.saveProfilePicture(userId, picture.submittedFileName, picture.inputStream)
    }
}

enum class GetAskTarget { SENDER, RECEIVER }

enum class GetUserCookieTarget {
    COLLECTED,
    COOKIES,
}

data class UserCategoryCreateDto(
    val categoryIdList: List<Long>,
)
