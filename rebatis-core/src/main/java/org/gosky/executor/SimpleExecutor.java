package org.gosky.executor;

import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.pool.ConnectionPool;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Auther: guozhong
 * @Date: 2019-03-10 23:25
 * @Description:
 */
public class SimpleExecutor implements Executor {

    private final ConnectionPool tConnectionPool;

    public SimpleExecutor(ConnectionPool tConnectionPool) {
        this.tConnectionPool = tConnectionPool;
    }

    @Override
    public CompletableFuture<QueryResult> query(String sql, List<?> values) {
        return tConnectionPool.sendPreparedStatement(sql, values);
    }

}

