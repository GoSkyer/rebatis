package org.gosky.parsing;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

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

    protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    protected ObjectFactory objectFactory = new DefaultObjectFactory();
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

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

//        ArrayMap<String, Integer> positionMap = paramPositionMap(parameters);
//        List<ParameterRange> rangeList = paramInSqlPosition(sqlBeforeParse);
        //解析方法参数
        ParamNameResolver paramNameResolver = new ParamNameResolver(method);
        Object parameter = paramNameResolver.getNamedParams(args);
        //parameterMappings 如果多参数那么是个map,如果单参数且不是list/array 就是原对象

        return replaceSqlWithParameters(sqlBeforeParse, parameter);

    }

    private ParseSqlResult replaceSqlWithParameters(String sqlBeforeParse, Object parameter) {
        List<String> rangeList = new ArrayList<>();
        GenericTokenParser parser = new GenericTokenParser("#{", "}", content -> {
            rangeList.add(content);
            return "?";
        });

        String sqlAfterParse = parser.parse(sqlBeforeParse);
        List<Object> values = new LinkedList<>();

        for (String range : rangeList) {
            if (typeList.contains(parameter.getClass())){
                //单参数java基础类型
                values.add(parameter);
            } else {
                //pojo或者map
                Object value = MetaObject.forObject(parameter, objectFactory, objectWrapperFactory, reflectorFactory).getValue(range);
                values.add(value);
            }

        }

        return new ParseSqlResult(sqlAfterParse, values);

    }

//    private ArrayMap<String, Integer> paramPositionMap(Parameter[] parameters) {
//
//        ArrayMap<String, Integer> positionMap = new ArrayMap<>(parameters.length);
//
//        for (int i = 0; i < parameters.length; i++) {
//            Parameter parameter = parameters[i];
//            Param param = parameter.getAnnotation(Param.class);
//
//            if (param == null) continue;
//
//            positionMap.put(param.value(), i);
//        }
//
//        return positionMap;
//    }

//    private List<ParameterRange> paramInSqlPosition(String sqlBeforeParse) throws Exception {
//
//        List<ParameterRange> list = new LinkedList<>();
//
//        char[] sql = sqlBeforeParse.toCharArray();
//
//        int start = 0;
//        int end = 0;
//
//        while (start < sql.length) {
//
//            while (end < sql.length && sql[end] != '}') end++;
//            while (start < sql.length && sql[start] != '#') start++;
//
//            if (end == sql.length || start == sql.length) break;
//
//            if (sql[start] == '#' && sql[end] == '}') {
//
//                if (sql[start + 1] != '{') throw new Exception("SQL参数配置错误");
//
//                ParameterRange range = new ParameterRange();
//                range.setStart(start);
//                range.setEnd(end);
//                range.setValue(sqlBeforeParse.substring(start + 2, end));
//                list.add(range);
//
//                end++;
//                start = end;
//
//            }
//
//        }
//
//        return list;
//    }

}
