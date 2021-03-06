package org.gosky.basemapper;

import org.apache.ibatis.reflection.MetaObject;

import java.util.Set;

public class BaseMapperProvider extends MapperTemplate {


    public String selectOne(Class<?> mapper, MetaObject metaObject) {
        Class<?> entityClass = getEntityClass(mapper);
        //修改返回值类型为实体类型
//        setResultType(ms, entityClass);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.whereAllIfColumns(entityClass, metaObject));
        return sql.toString();
    }


    public String selectByPrimaryKey(Class<?> mapper, MetaObject metaObject) {
        final Class<?> entityClass = getEntityClass(mapper);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.wherePKColumns());
        return sql.toString();
    }

    /**
     * 查询
     *
     * @param mapper
     * @return
     */
    public String select(Class<?> mapper, MetaObject metaObject) {
        Class<?> entityClass = getEntityClass(mapper);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.selectAllColumns(entityClass));
        sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.whereAllIfColumns(entityClass, metaObject));
        return sql.toString();
    }


    public String insert(Class<?> mapper, MetaObject metaObject) {
        Class<?> entityClass = getEntityClass(mapper);
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(trim(_sql -> {
            for (EntityColumn column : columnList) {
                _sql.append(SqlHelper.getIfNotNull(column, column.getColumn() + ",", metaObject));
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

    public String save(Class<?> mapper, MetaObject metaObject) {
        Class<?> entityClass = getEntityClass(mapper);
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        sql.append(SqlHelper.replaceIntoTable(entityClass, tableName(entityClass)));
        sql.append(trim(_sql -> {
            for (EntityColumn column : columnList) {
                _sql.append(SqlHelper.getIfNotNull(column, column.getColumn() + ",", metaObject));
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


    public String updateByPrimaryKey(Class<?> mapper, MetaObject metaObject) {
        Class<?> entityClass = getEntityClass(mapper);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.updateSetColumns(entityClass, metaObject, false));
        sql.append(SqlHelper.wherePKColumns());
        return sql.toString();
    }

    public String updateByPrimaryKeyWithNull(Class<?> mapper, MetaObject metaObject) {
        Class<?> entityClass = getEntityClass(mapper);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.updateSetColumns(entityClass, metaObject, true));
        sql.append(SqlHelper.wherePKColumns());
        return sql.toString();
    }


}
