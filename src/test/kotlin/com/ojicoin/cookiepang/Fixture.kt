package com.ojicoin.cookiepang

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KFixtureMonkey
import org.springframework.boot.test.context.SpringBootTest

val fixture: FixtureMonkey = KFixtureMonkey.create()

@SpringBootTest(classes = [CookiepangApplication::class])
abstract class SpringContextFixture {
    val fixture: FixtureMonkey = com.ojicoin.cookiepang.fixture
}
