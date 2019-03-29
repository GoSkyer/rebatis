package org.gosky.mapping;

import org.gosky.annotations.Mapper;
import org.gosky.annotations.Update;

@Mapper
public interface UserService {

    @Update("update set interfaceName  #{interfaceName} where id =#{}")
    void update(String name, int id);
}
