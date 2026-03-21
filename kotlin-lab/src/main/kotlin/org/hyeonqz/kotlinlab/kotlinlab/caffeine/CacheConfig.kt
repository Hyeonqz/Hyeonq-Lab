package org.hyeonqz.kotlinlab.kotlinlab.caffeine

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        return SimpleCacheManager().apply {
            setCaches(listOf(
                buildCache("caffeine_test_001", maxSize = 1_000, ttlSeconds = 60),
                buildCache("caffeine_test_002", maxSize = 1_0000, ttlSeconds = 30),
                buildCache("caffeine_test_003", maxSize = 1_000_000, ttlSeconds = 120),
            ))
        }
    }

    private fun buildCache(
        name: String,
        maxSize: Long,
        ttlSeconds: Long
    ): CaffeineCache {
        val cache = Caffeine.newBuilder()
            // ★ 핵심 1: 최대 엔트리 수 제한
            // 항목 1,000개 초과 시 LFU 방식으로 자동 evict
            .maximumSize(maxSize)
            // ★ 핵심 2: 짧은 TTL (Redis 장애 시에만 잠깐 버티는 용도)
            .expireAfterWrite(Duration.ofSeconds(ttlSeconds))
            // ★ 핵심 3: 메모리 기반 eviction (크기 제한과 함께 사용)
            // Window TinyLFU 알고리즘 — 최적 히트율 자동 유지
            // 캐시 히트율 모니터링용
            .recordStats()
            .build<Any, Any>()

        return CaffeineCache(name, cache)
    }

}