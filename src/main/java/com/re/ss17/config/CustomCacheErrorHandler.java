package com.re.ss17.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

public class CustomCacheErrorHandler implements CacheErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Redis connection error, falling back to DB - cache={} key={} loi={}",
                cache.getName(), key, exception.getMessage());
        log.debug("Chi tiet loi cache get", exception);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.warn("Bo qua cache put - cache={} key={} loi={}", cache.getName(), key, exception.getMessage());
        log.debug("Chi tiet loi cache put", exception);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Bo qua cache evict - cache={} key={} loi={}", cache.getName(), key, exception.getMessage());
        log.debug("Chi tiet loi cache evict", exception);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("Bo qua cache clear - cache={} loi={}", cache.getName(), exception.getMessage());
        log.debug("Chi tiet loi cache clear", exception);
    }
}
