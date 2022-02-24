package com.ojicoin.cookiepang

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus

@Controller
class HealthCheckController {
    @GetMapping("/health")
    @ResponseStatus(HttpStatus.OK)
    fun healthCheck() {
    }
}

@SpringBootApplication
class CookiepangApplication

fun main(args: Array<String>) {
    runApplication<CookiepangApplication>(*args)
}
