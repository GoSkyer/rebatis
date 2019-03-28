package org.gosky.rebatis.sample.mapper;

import com.github.jasync.sql.db.QueryResult;

import java.util.concurrent.CompletableFuture;

/**
 * @Auther: guozhong
 * @Date: 2019-03-28 18:10
 * @Description:
 */


public interface TestMapper {
    CompletableFuture<QueryResult> test(String sql);
}
