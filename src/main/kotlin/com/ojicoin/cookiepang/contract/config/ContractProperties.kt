package com.ojicoin.cookiepang.contract.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("contract")
data class ContractProperties(
    val address: String
)
