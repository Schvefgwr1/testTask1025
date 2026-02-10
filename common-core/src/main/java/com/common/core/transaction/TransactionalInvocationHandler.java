package com.common.core.transaction;

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
        if (method.isAnnotationPresent(Transactional.class)) {
            return executeInTransaction(method, args);
        } else {
            return invokeMethod(method, args);
        }
    }

    private Object executeInTransaction(Method method, Object[] args) throws Throwable {
        boolean originalAutoCommit = true;

        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            Object result = invokeMethod(method, args);
            connection.commit();

            return result;

        } catch (Throwable e) {
            try {
                connection.rollback();
                System.err.println("Transaction rolled back for method: " + method.getName());
            } catch (SQLException rollbackEx) {
                System.err.println("Failed to rollback transaction: " + rollbackEx.getMessage());
            }
            throw e;
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException e) {
                System.err.println("Failed to restore autoCommit mode: " + e.getMessage());
            }
        }
    }

    private Object invokeMethod(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
