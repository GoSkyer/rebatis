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

    @Select("select * from user where name = #{name} and age = #{age} and sex = 1")
    Observable<List<User>> test(@Param("age") int age, @Param("name") String name);

    @Insert("INSERT INTO user (username, password) VALUES ('test3', '12321233')")
    Call<Void> insert();

}
