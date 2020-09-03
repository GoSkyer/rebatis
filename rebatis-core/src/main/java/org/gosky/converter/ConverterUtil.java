package org.gosky.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import org.gosky.common.ReturnTypeEnum;
import org.gosky.common.SQLType;
import org.gosky.mapping.SqlFactory;
import org.gosky.util.TypeUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
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
                    ObjectNode objectNode = objectMapper.createObjectNode();
                    for (int i = 0; i < row.size(); i++) {
                        if (row.getValue(i) == null) {
                            objectNode.put(row.getColumnName(i), (String) null);
                        } else {
                            objectNode.put(row.getColumnName(i), row.getValue(i).toString());
                        }
                    }
                    try {
                        list.add(objectMapper.treeToValue(objectNode, (Class) dataType));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
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
                    return row.getValue(0);
                } else {
                    ObjectNode objectNode = objectMapper.createObjectNode();
                    for (int i = 0; i < row.size(); i++) {
                        objectNode.put(row.getColumnName(i), row.getValue(i).toString());
                    }
                    try {
                        return objectMapper.treeToValue(objectNode, (Class) type);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                }
            }
        } else {
            //异常
        }

        return null;
    }

}
