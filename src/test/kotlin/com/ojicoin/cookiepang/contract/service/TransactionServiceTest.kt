package com.ojicoin.cookiepang.contract.service

import com.ojicoin.cookiepang.SpringContextFixture
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.math.BigInteger

/**
 * @author user
 */
internal class TransactionServiceTest(
    @Autowired val sut: TransactionService
) : SpringContextFixture() {

    @Test
    fun test() {
        val blockNumber: BigInteger = sut.getBlockNumberByTxHash("0xb8b8119de7005a15606e35769290960ff8025916d034e3c8c65b7dd2b9f4094c")
        println(blockNumber)
    }
}
