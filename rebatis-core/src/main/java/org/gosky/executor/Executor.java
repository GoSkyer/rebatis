package org.gosky.executor;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Auther: guozhong
 * @Date: 2019-03-10 23:22
 * @Description:
 */
public interface Executor {

    CompletableFuture<RowSet<Row>> query(String sql, List<Object> values);

}
