package com.ojicoin.cookiepang.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Profile

@Profile("local")
@ConstructorBinding
@ConfigurationProperties("storage")
data class LocalStorageProperties(
    val domain: String
)

@Profile("!local")
@ConstructorBinding
@ConfigurationProperties("cloud.aws.s3")
data class S3Properties(
    val bucket: String,
    val directory: String,
)
