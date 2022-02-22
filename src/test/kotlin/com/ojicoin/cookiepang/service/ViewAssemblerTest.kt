package com.ojicoin.cookiepang.service

import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ojicoin.cookiepang.REPEAT_COUNT
import com.ojicoin.cookiepang.SpringContextFixture
import com.ojicoin.cookiepang.domain.Ask
import com.ojicoin.cookiepang.domain.AskStatus
import com.ojicoin.cookiepang.domain.Category
import com.ojicoin.cookiepang.domain.Cookie
import com.ojicoin.cookiepang.domain.CookieStatus
import com.ojicoin.cookiepang.domain.CookieStatus.DELETED
import com.ojicoin.cookiepang.domain.User
import com.ojicoin.cookiepang.repository.AskRepository
import com.ojicoin.cookiepang.repository.CategoryRepository
import com.ojicoin.cookiepang.repository.CookieRepository
import com.ojicoin.cookiepang.repository.UserRepository
import net.jqwik.api.Arbitraries
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.math.BigInteger

class ViewAssemblerTest(
    @Autowired val sut: ViewAssembler,
    @Autowired val askRepository: AskRepository,
    @Autowired val cookieRepository: CookieRepository,
    @Autowired val categoryRepository: CategoryRepository,
    @Autowired val userRepository: UserRepository,
    @Value("\${contract.address}") val contractAddress: String,
) : SpringContextFixture() {
    @Test
    fun cookieView() {
        val category = fixture.giveMeBuilder<Category>()
            .setNull("id")
            .sample()
        val categoryId = categoryRepository.save(category).id!!
        val creator = fixture.giveMeBuilder<User>()
            .setNull("id")
            .set("wallet_address", "a")
            .sample()
        val collector = fixture.giveMeBuilder<User>()
            .setNull("id")
            .set("wallet_address", "b")
            .sample()
        val authorUserId = userRepository.save(creator).id!!
        val ownedUserId = userRepository.save(collector).id!!
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("status", Arbitraries.of(CookieStatus::class.java).filter { it != DELETED })
            .set("authorUserId", authorUserId)
            .set("ownedUserId", ownedUserId)
            .set("categoryId", categoryId)
            .set("nftTokenId", BigInteger.valueOf(-1))
            .sample()
        val cookieId = cookieRepository.save(cookie).id!!

        // when
        val actual = sut.cookieView(viewerId = ownedUserId, cookieId = cookieId)

        then(actual.question).isEqualTo(cookie.title)
        then(actual.answer).isEqualTo(cookie.open(ownedUserId))
        then(actual.collectorName).isEqualTo(collector.nickname)
        then(actual.creatorName).isEqualTo(creator.nickname)
        then(actual.contractAddress).isEqualTo(contractAddress)
        then(actual.price).isEqualTo(cookie.price)
        then(actual.creatorProfileUrl).isEqualTo(creator.profileUrl)
        then(actual.collectorProfileUrl).isEqualTo(collector.profileUrl)
        then(actual.nftTokenId).isEqualTo(cookie.nftTokenId)
        then(actual.viewCount).isEqualTo(0L)
        then(actual.category.id).isEqualTo(category.id)
        then(actual.category.name).isEqualTo(category.name)
        then(actual.category.color).isEqualTo(category.color.name)
    }

    @Test
    fun cookieViewNotOwner() {
        val category = fixture.giveMeBuilder<Category>()
            .setNull("id")
            .sample()
        val categoryId = categoryRepository.save(category).id!!
        val creator = fixture.giveMeBuilder<User>()
            .setNull("id")
            .set("wallet_address", "a")
            .sample()
        val collector = fixture.giveMeBuilder<User>()
            .setNull("id")
            .set("wallet_address", "b")
            .sample()
        val viewer = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val authorUserId = userRepository.save(creator).id!!
        val ownedUserId = userRepository.save(collector).id!!
        val viewerId = userRepository.save(viewer).id!!
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("authorUserId", authorUserId)
            .set("ownedUserId", ownedUserId)
            .set("status", CookieStatus.ACTIVE)
            .set("categoryId", categoryId)
            .set("nftTokenId", BigInteger.valueOf(-1))
            .sample()
        val cookieId = cookieRepository.save(cookie).id!!

        // when
        val actual = sut.cookieView(viewerId = viewerId, cookieId = cookieId)

        then(actual.answer).isNull()
        then(actual.viewCount).isEqualTo(1L)
    }

    @Test
    fun timelineView() {
        val category = fixture.giveMeBuilder<Category>()
            .setNull("id")
            .sample()
        val categoryId = categoryRepository.save(category).id!!
        val collector = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val ownedUserId = userRepository.save(collector).id!!
        val author = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val authorUserId = userRepository.save(author).id!!
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("status", CookieStatus.ACTIVE)
            .set("ownedUserId", ownedUserId)
            .set("authorUserId", authorUserId)
            .set("categoryId", categoryId)
            .sample()
        cookieRepository.save(cookie).id!!

        // when
        val actual = sut.timelineView(viewerId = ownedUserId, viewCategoryId = cookie.categoryId)

        then(actual.contents).hasSize(1)
    }

    @Test
    fun timelineViewAllCategories() {
        val category = fixture.giveMeBuilder<Category>()
            .setNull("id")
            .sample()
        val categoryId = categoryRepository.save(category).id!!
        val collector = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val ownedUserId = userRepository.save(collector).id!!
        val author = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val authorUserId = userRepository.save(author).id!!
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("status", CookieStatus.ACTIVE)
            .set("ownedUserId", ownedUserId)
            .set("authorUserId", authorUserId)
            .set("categoryId", categoryId)
            .sample()
        cookieRepository.save(cookie).id!!

        // when
        val actual = sut.timelineView(viewerId = ownedUserId)

        then(actual.contents).hasSize(1)
    }

    @Test
    fun ownedCookiesView() {
        val category = fixture.giveMeBuilder<Category>()
            .setNull("id")
            .sample()
        val categoryId = categoryRepository.save(category).id!!
        val user = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val userId = userRepository.save(user).id!!
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("ownedUserId", userId)
            .set("categoryId", categoryId)
            .set("status", Arbitraries.of(CookieStatus::class.java).filter { it != DELETED })
            .sample()
        cookieRepository.save(cookie)

        val actual = sut.ownedCookiesView(userId = userId)
        then(actual.contents).hasSize(1)
    }

    @Test
    fun authorCookiesView() {
        val category = fixture.giveMeBuilder<Category>()
            .setNull("id")
            .sample()
        val categoryId = categoryRepository.save(category).id!!
        val user = fixture.giveMeBuilder<User>()
            .setNull("id")
            .sample()
        val userId = userRepository.save(user).id!!
        val cookie = fixture.giveMeBuilder<Cookie>()
            .setNull("id")
            .set("authorUserId", userId)
            .set("categoryId", categoryId)
            .set("status", Arbitraries.of(CookieStatus::class.java).filter { it != DELETED })
            .sample()
        cookieRepository.save(cookie)

        val actual = sut.authorCookiesView(userId = userId)

        then(actual.contents).hasSize(1)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun askViewAboutSender() {
        val ask = fixture.giveMeBuilder<Ask>()
            .setNull("id")
            .sample()

        askRepository.save(ask)

        val actual = sut.askViewAboutSender(userId = ask.senderId)

        then(actual.contents).hasSize(1)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun askViewAboutReceiver() {
        val ask = fixture.giveMeBuilder<Ask>()
            .setNull("id")
            .set("status", AskStatus.PENDING)
            .sample()

        askRepository.save(ask)

        val actual = sut.askViewAboutReceiver(userId = ask.receiverId)

        then(actual.contents).hasSize(1)
    }

    @RepeatedTest(REPEAT_COUNT)
    fun askViewAboutReceiverNotStatusPending() {
        val ask = fixture.giveMeBuilder<Ask>()
            .setNull("id")
            .set("status", Arbitraries.of(AskStatus.ACCEPTED, AskStatus.IGNORED, AskStatus.DELETED))
            .sample()

        askRepository.save(ask)

        val actual = sut.askViewAboutReceiver(userId = ask.receiverId)

        then(actual.contents).hasSize(0)
    }

    @AfterEach
    internal fun tearDown() {
        cookieRepository.deleteAll()
        categoryRepository.deleteAll()
        userRepository.deleteAll()
    }
}
