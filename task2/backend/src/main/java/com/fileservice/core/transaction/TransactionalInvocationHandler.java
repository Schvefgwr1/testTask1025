package com.fileservice.core.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Обработчик для перехвата вызовов методов и оборачивания их в транзакции
 */
public class TransactionalInvocationHandler implements InvocationHandler {
    private final Object target;
    private final Connection connection;

    public TransactionalInvocationHandler(Object target, Connection connection) {
        this.target = target;
        this.connection = connection;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Проверяем, помечен ли метод аннотацией @Transactional
        if (method.isAnnotationPresent(Transactional.class)) {
            return executeInTransaction(method, args);
        } else {
            return invokeMethod(method, args);
        }
    }

    /**
     * Выполняет метод в рамках транзакции
     */
    private Object executeInTransaction(Method method, Object[] args) throws Throwable {
        boolean originalAutoCommit = true;
        
        try {
            // Сохраняем и отключаем autoCommit
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            
            // Вызываем реальный метод
            Object result = invokeMethod(method, args);
            
            // Коммитим транзакцию при успехе
            connection.commit();
            
            return result;
            
        } catch (Throwable e) {
            // Откатываем транзакцию при ошибке
            try {
                connection.rollback();
                System.err.println("Transaction rolled back for method: " + method.getName());
            } catch (SQLException rollbackEx) {
                System.err.println("Failed to rollback transaction: " + rollbackEx.getMessage());
            }
            throw e;
        } finally {
            // Восстанавливаем исходный режим autoCommit
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                System.err.println("Failed to restore autoCommit mode: " + e.getMessage());
            }
        }
    }

    /**
     * Вызывает метод на реальном объекте, обрабатывая InvocationTargetException
     */
    private Object invokeMethod(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            // Разворачиваем InvocationTargetException, чтобы получить реальное исключение
            throw e.getCause();
        }
    }
}

