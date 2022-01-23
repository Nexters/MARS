package com.ojicoin

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KFixtureMonkeyBuilder
import com.navercorp.fixturemonkey.kotlin.generator.PrimaryConstructorArbitraryGenerator

const val REPEATED_COUNT = 3

val fixture: FixtureMonkey = KFixtureMonkeyBuilder()
    .defaultGenerator(PrimaryConstructorArbitraryGenerator.INSTANCE)
    .build()
