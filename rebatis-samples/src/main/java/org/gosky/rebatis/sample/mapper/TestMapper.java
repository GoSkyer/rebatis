package org.gosky.rebatis.sample.mapper;

import org.gosky.annotations.Mapper;
import org.gosky.annotations.Select;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Auther: guozhong
 * @Date: 2019-03-28 18:10
 * @Description:
 */

@Mapper
public interface TestMapper {

    @Select("select id from user")
    CompletableFuture<List<Integer>> test();
}