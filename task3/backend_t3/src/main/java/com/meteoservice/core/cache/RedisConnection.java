package com.meteoservice.core.cache;

import com.meteoservice.core.config.Config;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Управляет подключением к Redis
 */
public class RedisConnection {
    @Getter
    private final JedisPool jedisPool;

    public RedisConnection(Config config) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        String password = config.getRedisPassword();
        
        if (password != null && !password.isEmpty()) {
            this.jedisPool = new JedisPool(
                poolConfig,
                config.getRedisHost(),
                config.getRedisPort(),
                2000,
                password
            );
        } else {
            this.jedisPool = new JedisPool(
                poolConfig,
                config.getRedisHost(),
                config.getRedisPort()
            );
        }

        // Проверяем подключение
        try (Jedis jedis = jedisPool.getResource()) {
            String response = jedis.ping();
            System.out.println("Redis connection established: " + response);
        } catch (Exception e) {
            System.err.println("Failed to connect to Redis: " + e.getMessage());
            throw new RuntimeException("Redis connection failed", e);
        }
    }

    /**
     * Получает Jedis клиент из пула
     */
    public Jedis getResource() {
        return jedisPool.getResource();
    }

    /**
     * Закрывает пул подключений
     */
    public void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
            System.out.println("Redis connection pool closed");
        }
    }
}

