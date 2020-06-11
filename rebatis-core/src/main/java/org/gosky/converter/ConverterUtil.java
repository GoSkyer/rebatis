package org.gosky.converter;

import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.RowData;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import org.gosky.common.ReturnTypeEnum;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.persistence.Entity;

import lombok.extern.slf4j.Slf4j;

/**
 * @Auther: guozhong
 * @Date: 2019-04-14 23:19
 * @Description:
 */

@Slf4j
public class ConverterUtil {
    private ConverterFactory converterFactory;
    private static ConverterUtil singleton;

    public ConverterUtil(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    public static ConverterUtil with(ConverterFactory converterFactory) {
        if (singleton == null) {
            synchronized (ConverterUtil.class) {
                if (singleton == null) {
                    singleton = new ConverterUtil(converterFactory);
                    converterFactory.init();
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
                JsonObject json = new JsonObject();
                for (int i = 0; i < row.size(); i++) {
                    json.getMap().put(row.getColumnName(i), row.getValue(i));
                }
                list.add(json.mapTo((Class) dataType));
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
                JsonObject json = new JsonObject();
                for (int i = 0; i < row.size(); i++) {
                    json.getMap().put(row.getColumnName(i), row.getValue(i));
                }
                json.mapTo((Class) type);
            }
        } else {
            //异常
        }

        return null;
    }

}
