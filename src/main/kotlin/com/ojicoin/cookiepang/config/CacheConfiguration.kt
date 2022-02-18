package com.ojicoin.cookiepang.config

import com.github.benmanes.caffeine.cache.Caffeine
import com.ojicoin.cookiepang.config.Caches.TRANSFER_INFO_BY_TX_HASH
import com.ojicoin.cookiepang.dto.TransferInfo
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class CacheConfiguration {
    @Bean
    fun transferInfoByTxHashCacheTemplate(cacheManager: CacheManager) =
        CacheTemplate<TransferInfo>(cacheManager.getCache(TRANSFER_INFO_BY_TX_HASH.name)!!)

    @Bean
    fun cacheManager(): CacheManager {
        val caches = listOf(
            CaffeineCache(
                TRANSFER_INFO_BY_TX_HASH.name,
                Caffeine.newBuilder().recordStats()
                    .maximumSize(1000L)
                    .expireAfterWrite(Duration.ofMinutes(1))
                    .build(),
            )
        )
        return SimpleCacheManager()
            .apply { this.setCaches(caches) }
    }
}

class CacheTemplate<T>(private val cache: Cache) {
    @Suppress("UNCHECKED_CAST")
    operator fun get(key: Any): T? = cache[key]?.get() as? T

    operator fun set(key: Any, value: T) = cache.put(key, value)

    fun evict(key: Any) = cache.evict(key)
}

enum class Caches { TRANSFER_INFO_BY_TX_HASH }
