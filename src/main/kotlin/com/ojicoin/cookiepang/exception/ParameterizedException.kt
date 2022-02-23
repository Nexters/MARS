package com.ojicoin.cookiepang.exception

abstract class ParameterizedException(message: String) : RuntimeException(message) {
    val parameters: MutableMap<String, Any> = mutableMapOf()

    fun with(key: String, value: Any): ParameterizedException {
        parameters[key] = value
        return this
    }
}

open class InvalidRequestException(message: String) : ParameterizedException(message)

class BlockNotFoundException(message: String) : ParameterizedException(message)

class InvalidBlockChainRequestException(contractAddress: String, message: String) : InvalidRequestException(message) {
    init {
        with("contractAddress", contractAddress)
    }
}

class InvalidDomainStatusException(
    domainType: String,
    message: String,
) : InvalidRequestException(message) {
    init {
        with("domainType", domainType)
    }
}

class DuplicateDomainException(domainType: String, message: String) : InvalidRequestException(message) {
    init {
        with("domainType", domainType)
    }
}

class ForbiddenRequestException(message: String) : ParameterizedException(message)
