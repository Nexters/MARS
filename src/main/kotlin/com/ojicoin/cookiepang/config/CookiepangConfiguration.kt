package com.ojicoin.cookiepang.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [LocalStorageProperties::class, ContractProperties::class, S3Properties::class])
class CookiepangConfiguration
