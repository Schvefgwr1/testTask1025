package com.fileservice.core.transaction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для пометки методов, которые должны выполняться в рамках транзакции БД
 * Помеченные методы автоматически оборачиваются в:
 * - setAutoCommit(false)
 * - commit() при успехе
 * - rollback() при ошибке
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
}

