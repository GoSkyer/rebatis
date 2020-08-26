/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2017 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.gosky.basemapper;


import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.reflection.MetaObject;
import org.gosky.util.MapperStringUtil;

import java.util.Set;

/**
 * 拼常用SQL的工具类
 *
 * @author liuzh
 * @since 2015-11-03 22:40
 */
public class SqlHelper {
    /**
     * insert into tableName - 动态表名
     *
     * @param entityClass
     * @param defaultTableName
     * @return
     */
    public static String insertIntoTable(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(defaultTableName);
        sql.append(" (");
        return sql.toString();
    }

    /**
     * 判断自动!=null的条件结构
     *
     * @param column
     * @param contents
     * @return
     */
    public static String getIfNotNull(EntityColumn column, String contents, MetaObject metaObject) {
        return getIfNotNull(null, column, contents, metaObject);
    }

    /**
     * 判断自动!=null的条件结构
     *
     * @param column
     * @param metaObject
     * @return
     */
    public static String getIfNotNull(String entityName, EntityColumn column, String contents, MetaObject metaObject) {
        StringBuilder sql = new StringBuilder();
        if (metaObject.getValue(column.getProperty()) != null) {
            if (MapperStringUtil.isNotEmpty(entityName)) {
                sql.append(entityName).append(".");
            }
            return sql.append(contents).toString();
        } else {
            return "";
        }
    }


    /**
     * select xxx,xxx...
     *
     * @param entityClass
     * @return
     */
    public static String selectAllColumns(Class<?> entityClass) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(getAllColumns(entityClass));
        sql.append(" ");
        return sql.toString();
    }

    /**
     * 获取所有查询列，如id,name,code...
     *
     * @param entityClass
     * @return
     */
    public static String getAllColumns(Class<?> entityClass) {
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        StringBuilder sql = new StringBuilder();
        for (EntityColumn entityColumn : columnSet) {
            sql.append(entityColumn.getColumn()).append(",");
        }
        return sql.substring(0, sql.length() - 1);
    }

    /**
     * from tableName - 动态表名
     *
     * @param entityClass
     * @param defaultTableName
     * @return
     */
    public static String fromTable(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append(" FROM ");
        sql.append(defaultTableName);
        sql.append(" ");
        return sql.toString();
    }


    /**
     * where所有列的条件，会判断是否!=null
     *
     * @param entityClass
     * @return
     */
    public static String whereAllIfColumns(Class<?> entityClass, MetaObject metaObject) {
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnSet) {
            sql.append(getIfNotNull(column, " AND " + column.getColumnEqualsHolder(), metaObject));
        }
        return " where " + StringUtils.strip(sql.toString().trim(), "AND");
    }


    /**
     * update tableName -
     *
     * @param entityClass
     * @param defaultTableName 默认表名
     * @return
     */
    public static String updateTable(Class<?> entityClass, String defaultTableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(defaultTableName);
        sql.append(" ");
        return sql.toString();
    }


    /**
     * update set列
     *
     * @param entityClass
     * @return
     */
    public static String updateSetColumns(Class<?> entityClass, MetaObject metaObject) {
        StringBuilder sql = new StringBuilder();
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnSet) {
            if (!column.isId()) {
                sql.append(SqlHelper.getIfNotNull(column, column.getColumnEqualsHolder(null) + ",", metaObject));
            }
        }
        return " set " + StringUtils.strip(sql.toString().trim(), "AND");
    }

}
