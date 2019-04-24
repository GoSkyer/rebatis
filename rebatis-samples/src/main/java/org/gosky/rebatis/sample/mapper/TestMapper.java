package org.gosky.rebatis.sample.mapper;

import org.gosky.adapter.DefaultCall;
import org.gosky.annotations.Insert;
import org.gosky.annotations.Mapper;
import org.gosky.annotations.Select;
import org.gosky.rebatis.sample.User;

import java.util.List;

/**
 * @Auther: guozhong
 * @Date: 2019-03-28 18:10
 * @Description:
 */

@Mapper
public interface TestMapper {

    @Select("select * from user")
    DefaultCall<List<User>> test();

    @Insert("INSERT INTO user (username, password) VALUES ('test3', '12321233')")
    DefaultCall<Void> insert();

}
