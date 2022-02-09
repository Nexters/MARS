package com.ojicoin.cookiepang.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("storage")
data class StorageProperties(
    val domain: String
)
