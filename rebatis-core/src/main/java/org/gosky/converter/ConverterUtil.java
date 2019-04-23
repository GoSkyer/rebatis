package org.gosky.converter;

import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.RowData;

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


    public Object convert(QueryResult qr, ReturnTypeEnum returnTypeEnum, Type type) throws Exception {
        ResultSet rows = qr.getRows();
        int size = rows.size();
        if (size == 0) {
            return null;
        }

        if (returnTypeEnum == ReturnTypeEnum.SINGLE) {
            return queryResultToObject(qr, (Class) type);
        } else if (returnTypeEnum == ReturnTypeEnum.VOID) {

        } else if (returnTypeEnum == ReturnTypeEnum.LIST) {
            return queryResultToListObject(qr, type);
        } else if (returnTypeEnum == ReturnTypeEnum.MAP) {
            return queryResultToMap(qr);
        } else {
            //异常
        }

        return null;
    }

//    @Nullable
//    private Object getObject(Type type, ResultSet rows) throws Exception {
//        if (type.equals(Map.class)) {
//            Map map = new HashMap();
//            for (String columnName : rows.columnNames()) {
//                map.put(columnName, rows.get(0).get(columnName));
//            }
//            return map;
//        }
//        Class primary = (Class) type;
//        if (primary.equals(Void.class)) {
//            return null;
//        } else if (Stream.of(((Class) type).getAnnotations()).map(Annotation::annotationType)
//                .filter(aClass -> aClass.equals(Entity.class)).toArray().length > 0) {
//            return converterFactory.convert(rows.get(0), primary);
//        } else {
//
//            return rowDataToObject(rows.get(0), primary, rows.columnNames());
//        }
//    }


    public <T> List<T> queryResultToListObject(QueryResult queryResult, Type type) throws Exception {
        Type dataType;
        if (type instanceof ParameterizedType) {
            dataType = ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            throw new IllegalStateException(type + " return type must be parameterized"
                    + " as " + type + "<Foo> or " + type + "<? extends Foo>");
        }

        final ResultSet rows = queryResult.getRows();
        List<String> columnNames = rows.columnNames();
        List list = new ArrayList<T>();
        for (RowData row : rows) {
            list.add(rowDataToObject(row, (Class) dataType, columnNames));
        }
        return list;
    }

    public <T> T queryResultToObject(QueryResult queryResult, Class<T> clazz) {
        final ResultSet rows = queryResult.getRows();
        List<String> columnNames = rows.columnNames();
        Iterator<RowData> iterator = rows.iterator();
        if (iterator.hasNext()) {
            try {
                return rowDataToObject(iterator.next(), clazz, columnNames);
            } catch (Exception e) {
                log.error("convert object error :{}", e);
            }
        }
        return null;
    }

    public Map<String, Object> queryResultToMap(QueryResult queryResult) {
        final ResultSet rows = queryResult.getRows();
        List<String> columnNames = rows.columnNames();
        Iterator<RowData> iterator = rows.iterator();
        if (iterator.hasNext()) {
            try {
                return rowDataToMap(iterator.next(), columnNames);
            } catch (Exception e) {
                log.error("convert object error :{}", e);
            }
        }
        return null;
    }


    public <T> T rowDataToObject(RowData rowData, Class<T> clazz, List<String> columnNames) throws Exception {
        Iterator<Object> iterable = rowData.iterator();
        //只查一个字段或者count时，这时返回类型可能是String这种非用户自定义的类型
        // 所以这个类如果是JDK内的类，以及joda里的类（可能查了时间字段），直接返回，因为它肯定不是用户自定义的那种model
        if (columnNames.size() == 1 && (clazz.getClassLoader() == null) || clazz.getName().startsWith("org.joda.time")) {
            Object item = iterable.next();
            if (item == null) {
                return null;
            } else if (!clazz.equals(item.getClass())) {
                throw new IllegalStateException(String.format("data type you want convert is %s ,but database return type is %s ,just change it", clazz, item.getClass()));
            } else {
                return (T) item;
            }
        }

        //是否是有entity注解的class
        if (Stream.of(clazz.getAnnotations()).map(Annotation::annotationType)
                .filter(aClass -> aClass.equals(Entity.class)).toArray().length > 0) {
            return (T) converterFactory.convert(rowData, clazz);
        }

        T t = clazz.newInstance();
        Field[] arrf = clazz.getDeclaredFields();
        //遍历属性
        for (Field f : arrf) {
            //设置忽略访问校验
            f.setAccessible(true);
            //为属性设置内容
            f.set(t, rowData.get(f.getName()));
        }
        return t;
    }


    public static Map<String, Object> rowDataToMap(RowData rowData, List<String> columnNames) {
        Map<String, Object> res = new HashMap<>();
        Iterator<Object> iterable = rowData.iterator();
        int index = 0;
        while (iterable.hasNext()) {
            Object item = iterable.next();
            res.put(columnNames.get(index), item);
            index++;
        }
        return res;
    }
}
