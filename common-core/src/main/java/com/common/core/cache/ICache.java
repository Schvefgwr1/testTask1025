package com.common.core.cache;

import java.util.Optional;

/**
 * Интерфейс кэша для хранения объектов с TTL
 */
public interface ICache<K> {
    <T> Optional<T> get(K key, Class<T> clazz);

    <T> void set(K key, T value, long ttlSeconds);

    void delete(K key);

    boolean exists(K key);

    void clear();
}
