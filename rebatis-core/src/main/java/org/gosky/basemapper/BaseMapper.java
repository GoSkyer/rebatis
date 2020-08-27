package org.gosky.basemapper;

import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.gosky.adapter.Call;

import java.util.List;

public interface BaseMapper<T> {

    /**
     * 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     *
     * @param record
     * @return
     */
    @SelectProvider(value = BaseMapperProvider.class, method = "selectOne")
    Call<T> selectOne(T record);

    /**
     * 根据主键字段进行查询，方法参数必须包含完整的主键属性，查询条件使用等号
     *
     * @param key
     * @return
     */
    @SelectProvider(value = BaseMapperProvider.class, method = "selectByPrimaryKey")
    T selectByPrimaryKey(Object key);


    /**
     * 根据实体中的属性值进行查询，查询条件使用等号
     *
     * @param record
     * @return
     */
    @SelectProvider(value = BaseMapperProvider.class, method = "select")
    List<T> select(T record);

    /**
     * 保存一个实体，null的属性不会保存，会使用数据库默认值
     *
     * @param record
     * @return
     */
    @InsertProvider(value = BaseMapperProvider.class, method = "insert")
    Call<Long> insert(T record);


    /**
     * 根据主键更新属性不为null的值
     *
     * @param record
     * @return
     */
    @UpdateProvider(value = BaseMapperProvider.class, method = "updateByPrimaryKey")
    Call<Integer> updateByPrimaryKey(T record);

}
