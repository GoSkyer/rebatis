package org.gosky.common;

import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.gosky.Rebatis;

public class BaseMapper {

    protected MySQLPool client;

    public BaseMapper(Rebatis rebatis) {
        this.client = rebatis.executor.gettConnectionPool();
    }
}
