package org.gosky.executor;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Auther: guozhong
 * @Date: 2019-03-10 23:25
 * @Description:
 */
public class SimpleExecutor implements Executor {

    private final MySQLPool tConnectionPool;

    public SimpleExecutor(MySQLPool tConnectionPool) {
        this.tConnectionPool = tConnectionPool;
    }

    @Override
    public CompletableFuture<RowSet<Row>> query(String sql, List<Object> values) {
        Future<RowSet<Row>> execute = Future.future();
        tConnectionPool.preparedQuery(sql).execute(Tuple.tuple(values), execute);
        return execute.toCompletionStage().toCompletableFuture();
    }

}

