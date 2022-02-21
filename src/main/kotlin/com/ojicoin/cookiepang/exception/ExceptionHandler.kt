package com.ojicoin.cookiepang.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.NativeWebRequest
import org.zalando.problem.Problem
import org.zalando.problem.ProblemBuilder
import org.zalando.problem.Status
import org.zalando.problem.spring.web.advice.ProblemHandling

@ControllerAdvice
class ExceptionHandler : ProblemHandling {
    @ExceptionHandler(InvalidRequestException::class)
    fun handleInvalidRequestException(
        exception: InvalidRequestException,
        request: NativeWebRequest,
    ): ResponseEntity<Problem> {
        val problemBuilder = Problem.builder()
            .withTitle(Status.BAD_REQUEST.reasonPhrase)
            .withDetail(exception.message)
            .withStatus(Status.BAD_REQUEST)
        applyAttribute(problemBuilder, exception)
        return create(exception, problemBuilder.build(), request)
    }

    @ExceptionHandler(DuplicateDomainException::class)
    fun handleDuplicateDomainException(
        exception: DuplicateDomainException,
        request: NativeWebRequest,
    ): ResponseEntity<Problem> {
        val problemBuilder = Problem.builder()
            .withTitle(Status.CONFLICT.reasonPhrase)
            .withDetail(exception.message)
            .withStatus(Status.CONFLICT)
        applyAttribute(problemBuilder, exception)
        return create(exception, problemBuilder.build(), request)
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchException(exception: NoSuchElementException, request: NativeWebRequest): ResponseEntity<Problem> {
        val problem: Problem = Problem.builder()
            .withTitle(Status.NOT_FOUND.reasonPhrase)
            .withStatus(Status.NOT_FOUND)
            .withDetail(exception.message)
            .build()
        return create(exception, problem, request)
    }

    private fun applyAttribute(builder: ProblemBuilder, e: ParameterizedException) {
        e.parameters.forEach { (key: String?, value: Any?) -> builder.with(key, value) }
    }
}
