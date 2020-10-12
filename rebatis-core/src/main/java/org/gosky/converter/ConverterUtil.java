package org.gosky.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.data.Numeric;
import org.apache.commons.lang3.math.NumberUtils;
import org.gosky.common.ReturnTypeEnum;
import org.gosky.common.SQLType;
import org.gosky.mapping.SqlFactory;
import org.gosky.util.TypeUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: guozhong
 * @Date: 2019-04-14 23:19
 * @Description:
 */

public class ConverterUtil {
    private static ConverterUtil singleton;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ConverterUtil() {
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        objectMapper.registerModule(javaTimeModule);
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
//        ResultSet rows = rowSet.

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
            Type dataType;
            if (type instanceof ParameterizedType) {
                dataType = ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                throw new IllegalStateException(type + " return type must be parameterized"
                        + " as " + type + "<Foo> or " + type + "<? extends Foo>");
            }
            List<Object> list = new ArrayList<>();
            rowSet.forEach(row -> {
                if (TypeUtil.typeList.contains(dataType)) {
                    list.add(row.getValue(0));
                } else {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < row.size(); i++) {
                        map.put(row.getColumnName(i), row.getValue(i));
                    }
                    if (((ParameterizedType) dataType).getRawType() == Map.class) {
                        list.add(fromValue(map, Map.class));
                    } else {
                        list.add(fromValue(map, (Class) dataType));
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
            RowIterator<Row> iterator = rowSet.iterator();
            if (iterator.hasNext()) {
                Row row = iterator.next();
                if (TypeUtil.typeList.contains(type)) {
                    if (row.getValue(0) != null && row.getValue(0) instanceof Numeric) {
                        return NumberUtils.createNumber(((Numeric) row.getValue(0)).toString());
                    }

                    return row.getValue(0);
                } else {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < row.size(); i++) {
                        map.put(row.getColumnName(i), row.getValue(i));
                    }
                    if (type instanceof ParameterizedType) {
                        return fromValue(map, (Class) ((ParameterizedType) type).getActualTypeArguments()[0]);
                    } else {
                        return fromValue(map, (Class) type);
                    }
                }
            }
        } else {
            //异常
        }

        return null;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public <T> T fromValue(Object json, Class<T> clazz) {
        T value = objectMapper.convertValue(json, clazz);
        if (clazz == Object.class) {
            value = (T) adapt(value);
        }
        return value;
    }

    private static Object adapt(Object o) {
        try {
            if (o instanceof List) {
                List list = (List) o;
                return new JsonArray(list);
            } else if (o instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) o;
                return new JsonObject(map);
            }
            return o;
        } catch (Exception e) {
            throw new DecodeException("Failed to decode: " + e.getMessage());
        }
    }
}
