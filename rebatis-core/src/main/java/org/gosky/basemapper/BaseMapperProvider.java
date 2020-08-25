package org.gosky.basemapper;

import org.apache.ibatis.reflection.MetaObject;

import java.util.Set;

public class BaseMapperProvider extends MapperTemplate {

    public String insert(Class<?> mapper, MetaObject metaObject) {
        Class<?> entityClass = getEntityClass(mapper);
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(trim(_sql -> {
            for (EntityColumn column : columnList) {
                _sql.append(SqlHelper.getIfNotNull(column, column.getProperty() + ",", metaObject));
            }
            return _sql.toString();
        }));
        sql.append(") VALUES( ");
        sql.append(trim(_sql -> {
            for (EntityColumn column : columnList) {
                _sql.append(SqlHelper.getIfNotNull(column, column.getColumnHolder(null, null, ","), metaObject));
            }
            return _sql.toString();
        }));
        sql.append(");");
        return sql.toString();
    }

}
