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
import org.gosky.util.MapperStringUtil;

/**
 * 数据库表对应的列
 *
 * @author liuzh
 */
public class EntityColumn {
    /**
     * 所属的table
     */
    private EntityTable table;
    /**
     * java对象的fieldName
     */
    private String property;
    /**
     * 数据库字段名
     */
    private String column;
    private Class<?> javaType;

    /**
     * 对应的字段信息
     */
    private EntityField entityField;


    public EntityColumn() {
    }

    /**
     * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler}+separator
     *
     * @param entityName
     * @param suffix
     * @param separator
     * @return
     */
    public String getColumnHolder(String entityName, String suffix, String separator) {
        StringBuffer sb = new StringBuffer("#{");
        if (MapperStringUtil.isNotEmpty(entityName)) {
            sb.append(entityName);
            sb.append(".");
        }
        sb.append(this.property);
        if (MapperStringUtil.isNotEmpty(suffix)) {
            sb.append(suffix);
        }

        sb.append("}");
        if (MapperStringUtil.isNotEmpty(separator)) {
            sb.append(separator);
        }
        return sb.toString();
    }

    /**
     * 返回格式如:#{entityName.age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName
     * @return
     */
    public String getColumnHolder(String entityName) {
        return getColumnHolder(entityName, null);
    }

    /**
     * 返回格式如:#{entityName.age+suffix,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName
     * @param suffix
     * @return
     */
    public String getColumnHolder(String entityName, String suffix) {
        return getColumnHolder(entityName, null, null);
    }

    /**
     * 返回格式如:colum = #{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @return
     */
    public String getColumnEqualsHolder() {
        return getColumnEqualsHolder(null);
    }

    /**
     * 返回格式如:colum = #{age,jdbcType=NUMERIC,typeHandler=MyTypeHandler}
     *
     * @param entityName
     * @return
     */
    public String getColumnEqualsHolder(String entityName) {
        return this.column + " = " + getColumnHolder(entityName);
    }


    public EntityColumn(EntityTable table) {
        this.table = table;
    }

    public EntityField getEntityField() {
        return entityField;
    }

    public void setEntityField(EntityField entityField) {
        this.entityField = entityField;
    }

    public EntityTable getTable() {
        return table;
    }

    public void setTable(EntityTable table) {
        this.table = table;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }
}
