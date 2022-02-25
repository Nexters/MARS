package com.ojicoin.cookiepang.contract.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("contract.addr")
data class ContractProperties(
    val cookie: String,
    val hammer: String
)
