package com.ojicoin.cookiepang.contract.service

import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.contract.dto.TransactionInfo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author user
 */
internal class TransactionServiceTest(
    @Autowired val sut: TransactionService
) : SpringContextFixture() {

    @Test
    fun test() {
        val transactinoInfo: TransactionInfo = sut.getTransactionInfoByTxHash("0xb8b8119de7005a15606e35769290960ff8025916d034e3c8c65b7dd2b9f4094c")
        println(transactinoInfo)
    }
}
