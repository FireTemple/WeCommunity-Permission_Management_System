package com.bohan.shiro;

import com.bohan.service.RedisService;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

public class RedisCacheManager implements CacheManager {
    @Autowired
    private RedisService redisService;
    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return new RedisCache<>(s,redisService);
    }
}
