package com.ojicoin.cookiepang.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [StorageProperties::class, ContractProperties::class])
class CookiepangConfiguration
