package org.gosky.mapping;

import org.gosky.annotations.Mapper;
import org.gosky.annotations.Select;
import org.gosky.annotations.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Update("update set interfaceName  #{interfaceName} where id =#{id}")
    void update(String name, int id);

    @Select("select * from user")
    List<Long> selectAll(String name, int id);

    @Select("select * from user")
    Map<String, String> selectMap();

    @Update("update set interfaceName")
    Integer update();

}
