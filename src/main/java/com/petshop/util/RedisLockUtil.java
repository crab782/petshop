package com.petshop.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Component
public class RedisLockUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 尝试获取分布式锁
     * @param lockKey 锁的键
     * @param requestId 请求ID，用于标识锁的持有者
     * @param expireTime 过期时间（毫秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 释放分布式锁
     * @param lockKey 锁的键
     * @param requestId 请求ID，用于验证锁的持有者
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
        return result != null && result > 0;
    }

    /**
     * 阻塞获取分布式锁
     * @param lockKey 锁的键
     * @param requestId 请求ID，用于标识锁的持有者
     * @param expireTime 过期时间（毫秒）
     * @param timeout 超时时间（毫秒）
     * @return 是否获取成功
     */
    public boolean lock(String lockKey, String requestId, long expireTime, long timeout) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < timeout) {
            if (tryLock(lockKey, requestId, expireTime)) {
                return true;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}