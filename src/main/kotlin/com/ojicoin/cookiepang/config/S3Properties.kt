package com.ojicoin.cookiepang.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("cloud.aws.s3")
data class S3Properties(
    val cdnUrl: String,
    val bucket: String,
    val directory: String,
)
