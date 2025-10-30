package com.meteoservice.core.cache;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Optional;

@AllArgsConstructor
public class RedisCache implements ICache<String> {
    private final JedisPool jedisPool;
    private final Gson gson;

    @Override
    public <T> Optional<T> get(String key, Class<T> clazz) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(key);
            if (json == null) {
                return Optional.empty();
            }
            
            T object = gson.fromJson(json, clazz);
            return Optional.ofNullable(object);
        } catch (Exception e) {
            System.err.println("Redis GET error for key '" + key + "': " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public <T> void set(String key, T value, long ttlSeconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = gson.toJson(value);
            jedis.setex(key, ttlSeconds, json);
        } catch (Exception e) {
            System.err.println("Redis SET error for key '" + key + "': " + e.getMessage());
            throw new RuntimeException("Failed to set cache value", e);
        }
    }

    @Override
    public void delete(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            System.err.println("Redis DELETE error for key '" + key + "': " + e.getMessage());
        }
    }

    @Override
    public boolean exists(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            System.err.println("Redis EXISTS error for key '" + key + "': " + e.getMessage());
            return false;
        }
    }

    @Override
    public void clear() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
        } catch (Exception e) {
            System.err.println("Redis CLEAR error: " + e.getMessage());
        }
    }
}

