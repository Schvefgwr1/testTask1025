package com.fileservice.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Базовый интерфейс для мапперов ResultSet -> Model
 * 
 * @param <T> Тип модели (User, Session, FileInfo и т.д.)
 */
public interface IMapper<T> {
    /**
     * Маппит строку ResultSet в объект модели
     * 
     * @param rs ResultSet с текущей позицией на нужной строке
     * @return Объект модели
     * @throws SQLException если произошла ошибка при чтении из ResultSet
     */
    T mapFromResultSet(ResultSet rs) throws SQLException;
    
    /**
     * Безопасный маппинг с автоматическим вызовом rs.next()
     * Возвращает null, если строк нет
     * 
     * @param rs ResultSet
     * @return Объект модели или null
     */
    T mapFromResultSetSafe(ResultSet rs);
}

