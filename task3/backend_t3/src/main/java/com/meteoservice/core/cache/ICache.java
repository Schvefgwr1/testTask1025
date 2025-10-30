package com.meteoservice.core.cache;

import java.util.Optional;

/**
 * Интерфейс кэша для хранения объектов с TTL
 * @param <K> тип ключа
 */
public interface ICache<K> {
    /**
     * Получает объект из кэша по ключу
     * 
     * @param key ключ
     * @param clazz класс объекта для десериализации
     * @param <T> тип возвращаемого объекта
     * @return Optional с объектом, если найден
     */
    <T> Optional<T> get(K key, Class<T> clazz);

    /**
     * Сохраняет объект в кэш с TTL
     * 
     * @param key ключ
     * @param value объект для сохранения
     * @param ttlSeconds время жизни в секундах
     * @param <T> тип сохраняемого объекта
     */
    <T> void set(K key, T value, long ttlSeconds);

    /**
     * Удаляет значение из кэша
     * 
     * @param key ключ
     */
    void delete(K key);

    /**
     * Проверяет, существует ли ключ в кэше
     * 
     * @param key ключ
     * @return true, если ключ существует
     */
    boolean exists(K key);

    /**
     * Очищает весь кэш
     */
    void clear();
}

