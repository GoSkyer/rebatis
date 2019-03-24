package org.gosky.executor;


import com.github.jasync.sql.db.Configuration;
import com.github.jasync.sql.db.Connection;
import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.mysql.pool.MySQLConnectionFactory;
import com.github.jasync.sql.db.pool.ConnectionPool;
import com.github.jasync.sql.db.pool.PoolConfiguration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: guozhong
 * @Date: 2019-03-10 23:25
 * @Description:
 */
public class SimpleExecutor implements Executor {
    @Override
    public CompletableFuture<QueryResult> query(String sql, Object parameter) {

        PoolConfiguration poolConfiguration = new PoolConfiguration(
                // maxObjects
                100,
                // maxIdle
                TimeUnit.MINUTES.toMillis(15),
                // maxQueueSize
                10_000,
                // validationInterval
                TimeUnit.SECONDS.toMillis(30)
        );
        Connection connection = new ConnectionPool<>(
                new MySQLConnectionFactory(new Configuration(
                        "root",
                        "localhost",
                        3306,
                        "123456",
                        "test"
                )), poolConfiguration);

        CompletableFuture<QueryResult> queryResultCompletableFuture = connection.sendPreparedStatement(sql);
        return queryResultCompletableFuture;
    }
}
