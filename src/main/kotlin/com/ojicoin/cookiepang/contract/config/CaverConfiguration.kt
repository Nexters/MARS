package com.ojicoin.cookiepang.contract.config

import com.klaytn.caver.Caver
import okhttp3.Credentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.web3j.protocol.http.HttpService

/**
 * @author seongchan.kang
 */
@Configuration
class CaverConfiguration(
    @Value("\${contract.kas.node-api-url}") val nodeApiUrl: String,
    @Value("#{systemEnvironment['CONTRACT_KAS_ACCESS_KEY_ID']}") val accessKeyId: String,
    @Value("#{systemEnvironment['CONTRACT_KAS_SECRET_ACCESS_KEY']}") val secretAccessKey: String,
    @Value("#{systemEnvironment['CONTRACT_ADMIN_PRIVE_KEY']}") val adminPrivateKey: String,
    @Value("\${contract.kas.chain-id}") val chainId: String,
) {
    @Bean
    fun caver(): Caver {
        val httpService = HttpService(nodeApiUrl)
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey))
        httpService.addHeader("x-chain-id", chainId)
        val caver = Caver(httpService)

        val singleKeyring = caver.wallet.keyring.createFromPrivateKey(adminPrivateKey)
        caver.wallet.add(singleKeyring)
        return caver
    }
}
