package org.gosky.parsing;

import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author: Galaxy
 * @date: 2019-05-26 15:10
 **/
public class Parser {

    private List<Class<?>> typeList = new ArrayList<Class<?>>() {{
        add(String.class);

        add(Byte.class);
        add(Long.class);
        add(Short.class);
        add(Integer.class);
        add(Integer.class);
        add(Double.class);
        add(Float.class);
        add(Boolean.class);

        add(Byte[].class);
        add(Long[].class);
        add(Short[].class);
        add(Integer[].class);
        add(Integer[].class);
        add(Double[].class);
        add(Float[].class);
        add(Boolean[].class);

        add(byte.class);
        add(long.class);
        add(short.class);
        add(int.class);
        add(int.class);
        add(double.class);
        add(float.class);
        add(boolean.class);

        add(byte[].class);
        add(long[].class);
        add(short[].class);
        add(int[].class);
        add(int[].class);
        add(double[].class);
        add(float[].class);
        add(boolean[].class);

        add(Date.class);
        add(BigDecimal.class);
        add(BigDecimal.class);
        add(BigInteger.class);
        add(Object.class);

        add(Date[].class);
        add(BigDecimal[].class);
        add(BigDecimal[].class);
        add(BigInteger[].class);
        add(Object[].class);

//        add(Map.class);
//        add(HashMap.class);
        add(List.class);
        add(ArrayList.class);
        add(Collection.class);
        add(Iterator.class);
    }};


    private static Parser INSTANCE = new Parser();

    public static ParseSqlResult parse(String sqlBeforeParse, Method method, Object[] args) throws Exception {

        if (args == null || args.length == 0) return new ParseSqlResult(sqlBeforeParse);

        return INSTANCE.parserSqlWithParameters(sqlBeforeParse, method, args);
    }

    private ParseSqlResult parserSqlWithParameters(String sqlBeforeParse, Method method, Object[] args) throws Exception {

        Parameter[] parameters = method.getParameters();

        if (parameters == null || parameters.length == 0) return new ParseSqlResult(sqlBeforeParse);

        //解析方法参数
        ParamNameResolver paramNameResolver = new ParamNameResolver(method);
        Object parameter = paramNameResolver.getNamedParams(args);
        //parameterMappings 如果多参数那么是个map,如果单参数且不是list/array 就是原对象

        return replaceSqlWithParameters(sqlBeforeParse, parameter);

    }

    private ParseSqlResult replaceSqlWithParameters(String sqlBeforeParse, Object parameter) {
        //sql中的#{name} 的list
        List<String> mapping = new ArrayList<>();
        GenericTokenParser parser = new GenericTokenParser("#{", "}", content -> {
            mapping.add(content);
            return "?";
        });

        String sqlAfterParse = parser.parse(sqlBeforeParse);
        List<Object> values = new LinkedList<>();

        for (String name : mapping) {
            if (typeList.contains(parameter.getClass())){
                //单参数java基础类型
                values.add(parameter);
            } else {
                //pojo或者map
                Object value = MetaObject.forObject(parameter).getValue(name);
                values.add(value);
            }

        }

        return new ParseSqlResult(sqlAfterParse, values);

    }

}
