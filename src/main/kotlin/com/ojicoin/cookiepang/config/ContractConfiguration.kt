/*
 * ContractConfiguration.java 2022. 02. 15
 *
 * Copyright 2022 NAVERBUSINESS PLATFORM Corp. All rights Reserved.
 * NAVERBUSINESS PLATFORM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.ojicoin.cookiepang.config

import com.klaytn.caver.Caver
import com.klaytn.caver.contract.Contract
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * @author seongchan.kang
 */
@Import(CaverConfiguration::class)
@Configuration
class ContractConfiguration(
    private val caver: Caver,
    @Value("\${contract.addr.cookie}") val cookieAddress: String,
    @Value("\${contract.addr.hammer}") val coinAddress: String,
) {
    var cookieABI: String = ContractABI.COOKIE_FACTORY
    var coinABI: String = ContractABI.HAMMER_COIN

    @Bean("cookieContract")
    fun cookieContract(): Contract {
        return try {
            caver.contract.create(cookieABI, cookieAddress)
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalStateException(e)
        }
    }

    @Bean("coinContract")
    fun coinContract(): Contract {
        return try {
            caver.contract.create(coinABI, coinAddress)
        } catch (e: Exception) {
            e.printStackTrace()
            throw IllegalStateException(e)
        }
    }
}
