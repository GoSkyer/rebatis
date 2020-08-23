package org.gosky.converter;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import org.gosky.common.ReturnTypeEnum;
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

    private ConverterUtil() {
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


    public Object convert(RowSet<Row> rowSet, ReturnTypeEnum returnTypeEnum, Type type) {
//        ResultSet rows = rowSet.
        int size = rowSet.size();
        if (size == 0) {
            return null;
        }

        if (returnTypeEnum == ReturnTypeEnum.VOID) {
            return null;
        } else if (returnTypeEnum == ReturnTypeEnum.LIST) {
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
                    JsonObject json = new JsonObject();
                    for (int i = 0; i < row.size(); i++) {
                        json.getMap().put(row.getColumnName(i), row.getValue(i));
                    }
                    list.add(json.mapTo((Class) dataType));
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
                    JsonObject json = new JsonObject();
                    for (int i = 0; i < row.size(); i++) {
                        json.getMap().put(row.getColumnName(i), row.getValue(i));
                    }
                    return json.mapTo((Class) type);
                }
            }
        } else {
            //异常
        }

        return null;
    }

}
