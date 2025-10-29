package com.fileservice.core.transaction;

import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * Фабрика для создания транзакционных прокси объектов
 */
public class TransactionalProxy {
    
    /**
     * Создает транзакционный прокси для сервиса
     * 
     * @param <T> Тип интерфейса сервиса
     * @param interfaceClass Интерфейс сервиса (например, IAuthService.class)
     * @param implementation Реализация сервиса (например, new AuthService(...))
     * @param connection JDBC Connection для управления транзакциями
     * @return Прокси объект, реализующий интерфейс и оборачивающий методы в транзакции
     */
    @SuppressWarnings("unchecked")
    public static <T> T wrap(Class<T> interfaceClass, T implementation, Connection connection) {
        if (interfaceClass == null) {
            throw new IllegalArgumentException("Interface class cannot be null");
        }
        if (implementation == null) {
            throw new IllegalArgumentException("Implementation cannot be null");
        }
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null");
        }
        
        // Создаем обработчик для перехвата вызовов
        TransactionalInvocationHandler handler = new TransactionalInvocationHandler(
            implementation, 
            connection
        );
        
        // Создаем прокси объект через Java Reflection API
        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[] { interfaceClass },
            handler
        );
    }
}

