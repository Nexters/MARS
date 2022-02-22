package com.ojicoin.cookiepang.contract.dto

import java.math.BigInteger

data class Price(
    val price: BigInteger
)

data class Answer(
    val answer: Boolean
)

data class TokenAddress(
    val tokenAddress: BigInteger
)

data class Amount(
    val amount: BigInteger
)

data class ContractAddress(
    val address: String
)

data class TransactionInfo(
    val blockHash: String,
    val blockNumber: BigInteger,
    val fromAddress: String,
    val senderTxHash: String,
    val txHash: String
)
