package com.ojicoin.cookiepang

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KFixtureMonkey
import org.springframework.boot.test.context.SpringBootTest

const val REPEAT_COUNT = 5

val fixture: FixtureMonkey = KFixtureMonkey.create()

@SpringBootTest(classes = [CookiepangApplication::class])
abstract class SpringContextFixture {
    val fixture: FixtureMonkey = com.ojicoin.cookiepang.fixture
}
