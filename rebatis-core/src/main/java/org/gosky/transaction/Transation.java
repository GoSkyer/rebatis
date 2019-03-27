package org.gosky.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 事务管理器接口
 */
public interface Transation {

    /**
     * Retrieve inner database connection
     *
     * @return DataBase connection
     * @throws SQLException
     */
    Connection getConnection() throws SQLException;

    /**
     * Commit inner database connection.
     *
     * @throws SQLException
     */
    void commit() throws SQLException;

    /**
     * Rollback inner database connection.
     *
     * @throws SQLException
     */
    void rollback() throws SQLException;

    /**
     * Close inner database connection.
     *
     * @throws SQLException
     */
    void close() throws SQLException;
}
