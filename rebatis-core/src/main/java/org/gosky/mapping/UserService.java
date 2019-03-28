package org.gosky.mapping;

import org.gosky.annotations.Mapper;
import org.gosky.annotations.Select;
import org.gosky.annotations.Update;

@Mapper
public interface UserService {

    @Update("update set name  #{name} where id =#{}")
    void update();
}
