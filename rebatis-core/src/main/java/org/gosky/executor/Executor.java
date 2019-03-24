package org.gosky.executor;

import com.github.jasync.sql.db.QueryResult;

import java.util.concurrent.CompletableFuture;

/**
 * @Auther: guozhong
 * @Date: 2019-03-10 23:22
 * @Description:
 */
public interface Executor {

    CompletableFuture<QueryResult> query(String sql, Object parameter);

}
