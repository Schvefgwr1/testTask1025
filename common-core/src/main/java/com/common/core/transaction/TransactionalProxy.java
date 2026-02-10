package com.common.core.transaction;

import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * Фабрика для создания транзакционных прокси объектов
 */
public class TransactionalProxy {

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

        TransactionalInvocationHandler handler = new TransactionalInvocationHandler(
                implementation,
                connection
        );

        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                handler
        );
    }
}
