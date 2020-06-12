package org.gosky.executor;

import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import java.util.List;

/**
 * @Auther: guozhong
 * @Date: 2019-03-10 23:22
 * @Description:
 */
public interface Executor {

    Future<RowSet<Row>> query(String sql, List<Object> values);

}
