package org.gosky.rebatis.sample.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.gosky.adapter.Call;
import org.gosky.basemapper.BaseMapper;
import org.gosky.rebatis.sample.User;

/**
 * @Auther: guozhong
 * @Date: 2019-03-28 18:10
 * @Description:
 */

@Mapper
public interface TestMapper extends BaseMapper<User> {

    @Select("select name from user where name = #{user.name} and age = #{user.age} and sex = 1 limit #{limit}")
    Call<String> test(@Param("user") User user, @Param("limit") Integer limit);

//    @Insert("INSERT INTO user (username, password) VALUES ('test3', '12321233')")
//    Call<Void> insert();

}
