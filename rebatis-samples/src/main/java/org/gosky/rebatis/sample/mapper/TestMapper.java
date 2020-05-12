package org.gosky.rebatis.sample.mapper;

import org.gosky.adapter.Call;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.gosky.rebatis.sample.User;

import java.util.List;

import io.reactivex.Observable;

/**
 * @Auther: guozhong
 * @Date: 2019-03-28 18:10
 * @Description:
 */

@Mapper
public interface TestMapper {

    @Select("select * from user where name = #{user.name} and age = #{user.age} and sex = 1 limit #{limit}")
    Observable<List<User>> test(@Param("user") User user, @Param("limit") Integer limit);

    @Insert("INSERT INTO user (username, password) VALUES ('test3', '12321233')")
    Call<Void> insert();

}
