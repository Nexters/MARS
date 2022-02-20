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
