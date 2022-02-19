package com.ojicoin.cookiepang.config

import com.ojicoin.cookiepang.contract.config.ContractProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(value = [LocalStorageProperties::class, ContractProperties::class, S3Properties::class])
class CookiepangConfiguration
