package org.gosky.rebatis.sample.mapper;

import com.github.jasync.sql.db.QueryResult;

import org.gosky.annotations.Mapper;
import org.gosky.annotations.Select;

import java.util.concurrent.CompletableFuture;

/**
 * @Auther: guozhong
 * @Date: 2019-03-28 18:10
 * @Description:
 */

@Mapper
public interface TestMapper {

    @Select("select * from user")
    CompletableFuture<QueryResult> test();
}
