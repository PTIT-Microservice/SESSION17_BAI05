package com.re.ss17;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.re.ss17.config.CustomCacheErrorHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.data.redis.RedisConnectionFailureException;

@DisplayName("BT05 - CustomCacheErrorHandler khong nem loi ra ngoai (silent fail)")
class CustomCacheErrorHandlerTest {

    private final CacheErrorHandler handler = new CustomCacheErrorHandler();
    private final Cache cache = mock(Cache.class);
    private final RuntimeException failure = new RedisConnectionFailureException("Redis down");

    @Test
    void getErrorKhongNemLoi() {
        handler.handleCacheGetError(failure, cache, 1L);
    }

    @Test
    void putErrorKhongNemLoi() {
        handler.handleCachePutError(failure, cache, 1L, "value");
    }

    @Test
    void evictErrorKhongNemLoi() {
        handler.handleCacheEvictError(failure, cache, 1L);
    }

    @Test
    void clearErrorKhongNemLoi() {
        handler.handleCacheClearError(failure, cache);
    }

    @Test
    void handlerMacDinhCuaSpringThiNemLoi() {
        assertThatThrownBy(() -> new org.springframework.cache.interceptor.SimpleCacheErrorHandler()
                .handleCacheGetError(failure, cache, 1L))
                .isSameAs(failure);
    }
}
