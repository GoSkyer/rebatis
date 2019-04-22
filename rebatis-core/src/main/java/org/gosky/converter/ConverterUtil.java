package org.gosky.converter;

import com.github.jasync.sql.db.QueryResult;
import com.github.jasync.sql.db.ResultSet;
import com.github.jasync.sql.db.RowData;

import org.gosky.common.ReturnTypeEnum;
import org.gosky.exception.ParseException;
import org.jetbrains.annotations.Nullable;

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

/**
 * @Auther: guozhong
 * @Date: 2019-04-14 23:19
 * @Description:
 */
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

        } else if (returnTypeEnum == ReturnTypeEnum.VOID) {

        } else if (returnTypeEnum == ReturnTypeEnum.LIST) {
            return queryResultToListObject(qr, type);
        } else if (returnTypeEnum == ReturnTypeEnum.MAP) {

        } else {
            //异常
        }

//        if (type instanceof ParameterizedType) {
//            //list or map model
//            Type dataContinerType = ((ParameterizedType) type).getRawType();
//            if (dataContinerType.equals(List.class)) {
//                Type dataType = ((ParameterizedType) type).getActualTypeArguments()[0];
//                if (dataType instanceof Class) {
//                    List list = new ArrayList();
//                    for (RowData row : qr.getRows()) {
//                        list.add(getObject(dataType, rows));
//                    }
//                    return list;
//                } else {
//                    throw new UnsupportTypeException(String.format("not support type : %s", dataContinerType));
//                }
//            } else if (dataContinerType.equals(Map.class)) {
//            } else {
//                throw new UnsupportTypeException(String.format("not support type : %s", dataContinerType));
//            }
//        } else if (type instanceof Class) {
//            return getObject(type, rows);
//        }
        return null;
    }

    @Nullable
    private Object getObject(Type type, ResultSet rows) throws Exception {
        if (type.equals(Map.class)) {
            Map map = new HashMap();
            for (String columnName : rows.columnNames()) {
                map.put(columnName, rows.get(0).get(columnName));
            }
            return map;
        }
        Class primary = (Class) type;
        if (primary.equals(Void.class)) {
            return null;
        } else if (Stream.of(((Class) type).getAnnotations()).map(Annotation::annotationType)
                .filter(aClass -> aClass.equals(Entity.class)).toArray().length > 0) {
            return converterFactory.convert(rows.get(0), primary);
        } else {

            return rowDataToObject(rows.get(0), primary, rows.columnNames());
        }
    }


    public <T> List<T> queryResultToListObject(QueryResult queryResult, Type type) throws Exception {

        Type dataType = ((ParameterizedType) type).getActualTypeArguments()[0];

        final ResultSet rows = queryResult.getRows();
        List<String> columnNames = rows.columnNames();
        List list = new ArrayList<T>();
        for (RowData row : rows) {
            list.add(rowDataToObject(row, (Class) dataType, columnNames));
        }
        return list;
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
                throw new ParseException(String.format("data type you want convert is %s ,but database return type is %s ,just change it", clazz, item.getClass()));
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
}
