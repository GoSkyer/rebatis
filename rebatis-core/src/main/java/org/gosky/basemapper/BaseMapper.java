package org.gosky.basemapper;

import org.apache.ibatis.annotations.SelectProvider;
import org.gosky.adapter.Call;

public interface BaseMapper<T> {

    /**
     * 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
     *
     * @param record
     * @return
     */
    @SelectProvider(value = BaseMapperProvider.class, method = "insert")
    Call<T> insert(T record);

}