package org.gosky.basemapper;


import org.gosky.util.MapperStringUtil;
import org.gosky.util.Style;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体类工具类 - 处理实体和数据库表以及字段关键的一个类
 */
public class EntityHelper {
    private static final Logger logger = LoggerFactory.getLogger(EntityHelper.class);
    /**
     * 实体类 => 表对象
     */
    private static final Map<Class<?>, EntityTable> entityTableMap = new ConcurrentHashMap<Class<?>, EntityTable>();
    /**
     * 初始化实体属性
     *
     * @param entityClass
     */
    public static synchronized void initEntityNameMap(Class<?> entityClass) {
        if (entityTableMap.get(entityClass) != null) {
            return;
        }
        //创建并缓存EntityTable
        EntityTable entityTable = resolveEntity(entityClass);
        entityTableMap.put(entityClass, entityTable);
    }

    /**
     * 获取表对象
     *
     * @param entityClass
     * @return
     */
    public static EntityTable getEntityTable(Class<?> entityClass) {
        EntityTable entityTable = entityTableMap.get(entityClass);
        if (entityTable == null) {
            throw new MapperException("无法获取实体类" + entityClass.getCanonicalName() + "对应的表名!");
        }
        return entityTable;
    }

    /**
     * 获取全部列
     *
     * @param entityClass
     * @return
     */
    public static Set<EntityColumn> getColumns(Class<?> entityClass) {
        return getEntityTable(entityClass).getEntityClassColumns();
    }


    public static EntityTable resolveEntity(Class<?> entityClass) {

        //创建并缓存EntityTable
        EntityTable entityTable = null;
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            if (!"".equals(table.name())) {
                entityTable = new EntityTable(entityClass);
                entityTable.setTable(table);
            }
        }
        if (entityTable == null) {
            entityTable = new EntityTable(entityClass);
            //可以通过stye控制
            String tableName = MapperStringUtil.convertByStyle(entityClass.getSimpleName(), Style.camelhumpAndLowercase);
            //自动处理关键字
            if (SqlReservedWords.containsWord(tableName)) {
                tableName = MessageFormat.format(SqlReservedWords.wrapKeyword, tableName);
            }
            entityTable.setName(tableName);
        }
        entityTable.setEntityClassColumns(new LinkedHashSet<EntityColumn>());
//        entityTable.setEntityClassPKColumns(new LinkedHashSet<EntityColumn>());
        //处理所有列
        List<EntityField> fields = null;

        fields = FieldHelper.getFields(entityClass);
        for (EntityField field : fields) {
            //如果启用了简单类型，就做简单类型校验，如果不是简单类型，直接跳过
            //3.5.0 如果启用了枚举作为简单类型，就不会自动忽略枚举类型
            //4.0 如果标记了 Column 或 ColumnType 注解，也不忽略
            if (SimpleTypeUtil.isSimpleType(field.getJavaType())) {
                continue;
            }
            processField(entityTable, field);
        }
        //当pk.size=0的时候使用所有列作为主键
        if (entityTable.getEntityClassPKColumns().size() == 0) {
            entityTable.setEntityClassPKColumns(entityTable.getEntityClassColumns());
        }
//        entityTable.initPropertyMap();
        return entityTable;
    }


    /**
     * 处理字段
     *
     * @param entityTable
     * @param field
     */
    protected static void processField(EntityTable entityTable, EntityField field) {
        //排除字段
        if (field.isAnnotationPresent(Transient.class)) {
            return;
        }
        //Id
        EntityColumn entityColumn = new EntityColumn(entityTable);
        //是否使用 {xx, javaType=xxx}
//        entityColumn.setUseJavaType(config.isUseJavaType());
        //记录 field 信息，方便后续扩展使用
        entityColumn.setEntityField(field);
//        if (field.isAnnotationPresent(Id.class)) {
//            entityColumn.setId(true);
//        }
        //Column
        String columnName = null;
//        if (field.isAnnotationPresent(Column.class)) {
//            Column column = field.getAnnotation(Column.class);
//            columnName = column.name();
//            entityColumn.setUpdatable(column.updatable());
//            entityColumn.setInsertable(column.insertable());
//        }
        //ColumnType
//        if (field.isAnnotationPresent(ColumnType.class)) {
//            ColumnType columnType = field.getAnnotation(ColumnType.class);
//            //是否为 blob 字段
//            entityColumn.setBlob(columnType.isBlob());
//            //column可以起到别名的作用
//            if (StringUtil.isEmpty(columnName) && StringUtil.isNotEmpty(columnType.column())) {
//                columnName = columnType.column();
//            }
//            if (columnType.jdbcType() != JdbcType.UNDEFINED) {
//                entityColumn.setJdbcType(columnType.jdbcType());
//            }
//            if (columnType.typeHandler() != UnknownTypeHandler.class) {
//                entityColumn.setTypeHandler(columnType.typeHandler());
//            }
//        }
        //列名
        if (MapperStringUtil.isEmpty(columnName)) {
            columnName = MapperStringUtil.convertByStyle(field.getName(), Style.camelhumpAndLowercase);
        }
        //自动处理关键字
        if (SqlReservedWords.containsWord(columnName)) {
            columnName = MessageFormat.format(SqlReservedWords.wrapKeyword, columnName);
        }
        entityColumn.setProperty(field.getName());
        entityColumn.setColumn(columnName);
        entityColumn.setJavaType(field.getJavaType());
        if (field.getJavaType().isPrimitive()) {
            logger.warn("通用 Mapper 警告信息: <[" + entityColumn + "]> 使用了基本类型，基本类型在动态 SQL 中由于存在默认值，因此任何时候都不等于 null，建议修改基本类型为对应的包装类型!");
        }
        //OrderBy
//        processOrderBy(entityTable, field, entityColumn);
        //处理主键策略
//        processKeyGenerator(entityTable, field, entityColumn);
        entityTable.getEntityClassColumns().add(entityColumn);
        if (entityColumn.getColumn().equals("id")) {
            entityTable.getEntityClassPKColumns().add(entityColumn);
        }
    }
}