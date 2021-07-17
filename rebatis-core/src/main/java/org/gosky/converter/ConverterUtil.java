package org.gosky.converter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.TypeUtil;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.data.Numeric;
import org.apache.commons.lang3.math.NumberUtils;
import org.gosky.common.ReturnTypeEnum;
import org.gosky.common.SQLType;
import org.gosky.mapping.SqlFactory;
import org.gosky.util.TypeConstants;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @Auther: guozhong
 * @Date: 2019-04-14 23:19
 * @Description:
 */

public class ConverterUtil {
    private static ConverterUtil singleton;
//    private final ObjectMapper objectMapper = new ObjectMapper();

    private ConverterUtil() {
//        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        JavaTimeModule javaTimeModule = new JavaTimeModule();
//        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
//        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
//        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        objectMapper.registerModule(javaTimeModule);
    }

    public static ConverterUtil with() {
        if (singleton == null) {
            synchronized (ConverterUtil.class) {
                if (singleton == null) {
                    singleton = new ConverterUtil();
                }
            }
        }
        return singleton;
    }


    public Object convert(SqlFactory sqlFactory, RowSet<Row> rowSet, ReturnTypeEnum returnTypeEnum, Type type) {
        if (returnTypeEnum == ReturnTypeEnum.VOID) {
            return null;
        }

        if (sqlFactory.getSqlType() == SQLType.INSERT) {
            return rowSet.property(MySQLClient.LAST_INSERTED_ID);
        } else if (sqlFactory.getSqlType() == SQLType.UPDATE || sqlFactory.getSqlType() == SQLType.DELETE) {
            return rowSet.rowCount();
        }

        int size = rowSet.size();
        if (size == 0) {
            return null;
        }

        if (returnTypeEnum == ReturnTypeEnum.LIST) {
            Class<?> actualClass = TypeUtil.getClass(TypeUtil.getTypeArgument(type));
            List<Object> list = new ArrayList<>();
            rowSet.forEach(row -> {
                if (TypeConstants.typeList.contains(actualClass)) {
                    list.add(row.getValue(0));
                } else {
                    if (actualClass == Map.class) {
                        list.add(fromValue(row, Map.class));
                    } else {
                        list.add(fromValue(row, actualClass));
                    }
                }
            });

            return list;
        } else if (returnTypeEnum == ReturnTypeEnum.MAP) {
            RowIterator<Row> iterator = rowSet.iterator();
            if (iterator.hasNext()) {
                Row row = iterator.next();
                Map<String, Object> res = new HashMap<>();
                for (int i = 0; i < row.size(); i++) {
                    res.put(row.getColumnName(i), row.getValue(i));
                }
                return res;
            }
            return null;
        } else if (returnTypeEnum == ReturnTypeEnum.SINGLE) {
            Class<?> actualClass;
            if (type instanceof ParameterizedType) {
                actualClass = TypeUtil.getClass(TypeUtil.getTypeArgument(type));
            } else {
                actualClass = TypeUtil.getClass(type);
            }
            RowIterator<Row> iterator = rowSet.iterator();
            if (iterator.hasNext()) {
                Row row = iterator.next();
                if (TypeConstants.typeList.contains(actualClass)) {
                    return Convert.convertWithCheck(actualClass, row.getValue(0), null, false);
                } else {
                    return fromValue(row, actualClass);
                }
            }
        } else {
            //异常
        }

        return null;
    }

//    public ObjectMapper getObjectMapper() {
//        return objectMapper;
//    }

    public <T> T fromValue(Row row, Class<T> clazz) {
//        T value = objectMapper.convertValue(json, clazz);
//        if (clazz == Object.class) {
//            value = (T) adapt(value);
//        }
        T t = ReflectUtil.newInstanceIfPossible(clazz);
        if (t instanceof Map) {
            for (int i = 0; i < row.size(); i++) {
                BeanUtil.setFieldValue(t, StrUtil.toCamelCase(row.getColumnName(i)), row.getValue(i));
            }
        } else {
            t = BeanUtil.fillBean(ReflectUtil.newInstanceIfPossible(clazz), new ValueProvider<String>() {
                @Override
                public Object value(String s, Type type) {
                    try {
                        return row.getValue(StrUtil.toUnderlineCase(s));
                    } catch (Exception e) {
                        if (e instanceof NoSuchElementException) {
                            return null;
                        } else {
                            throw e;
                        }
                    }
                }

                @Override
                public boolean containsKey(String s) {
                    return true;
                }
            }, new CopyOptions().ignoreNullValue().ignoreCase());
        }
        return t;

    }

//    private static Object adapt(Object o) {
//        try {
//            if (o instanceof List) {
//                List list = (List) o;
//                return new JsonArray(list);
//            } else if (o instanceof Map) {
//                @SuppressWarnings("unchecked")
//                Map<String, Object> map = (Map<String, Object>) o;
//                return new JsonObject(map);
//            }
//            return o;
//        } catch (Exception e) {
//            throw new DecodeException("Failed to decode: " + e.getMessage());
//        }
//    }
}
