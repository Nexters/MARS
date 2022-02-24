package com.ojicoin.cookiepang.config

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.util.Enumeration
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.system.measureTimeMillis

@Configuration
@Profile(value = ["local", "dev", "real"])
class WebConfiguration : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("*")
            .allowedMethods(
                HttpMethod.GET.name,
                HttpMethod.HEAD.name,
                HttpMethod.POST.name,
                HttpMethod.PUT.name,
                HttpMethod.DELETE.name
            )
    }
}

@Component
class LoggingFilter : Filter {
    private val objectMapper = jacksonObjectMapper()
    private val log: Logger = LoggerFactory.getLogger(LoggingFilter::class.java)

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val cachedRequest = ContentCachingRequestWrapper(request as HttpServletRequest)
        val cachedResponse = ContentCachingResponseWrapper(response as HttpServletResponse)

        val time = measureTimeMillis { chain.doFilter(cachedRequest, cachedResponse) }

        val requestBody = cachedRequest.contentAsByteArray
        val responseBody = cachedResponse.contentAsByteArray

        val requestBodyJson = try {
            objectMapper.readTree(requestBody).toString()
        } catch (e: JsonParseException) {
            requestBody.toString()
        }

        val responseBodyJson = try {
            objectMapper.readTree(responseBody).toString()
        } catch (e: JsonParseException) {
            responseBody.toString()
        }

        val loggingMessage = """ [REQUEST] ${request.method} ${request.requestURI} ${cachedResponse.status} - ${time}ms
                Headers : ${getHeaders(request)}
                RequestBody : $requestBodyJson
                ResponseBody : $responseBodyJson
        """.trimIndent()

        if (cachedResponse.status >= 400) {
            log.error(loggingMessage)
        } else {
            log.info(loggingMessage)
        }
        cachedResponse.copyBodyToResponse()
    }

    private fun getHeaders(request: HttpServletRequest): Map<String, *> {
        val headerMap = mutableMapOf<String, Any>()
        val headerArray: Enumeration<String> = request.headerNames
        while (headerArray.hasMoreElements()) {
            val headerName = headerArray.nextElement() as String
            headerMap[headerName] = request.getHeader(headerName)
        }
        return headerMap
    }
}
