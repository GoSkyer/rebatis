package org.gosky.parsing;

import org.gosky.annotations.Param;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @description: TODO
 * @author: Galaxy
 * @date: 2019-05-26 15:10
 **/
public class Parser {

    private static Parser INSTANCE = new Parser();

    @Deprecated
    private Map<Object, Object> sqlCacheMap = new HashMap<>();

    public static ParseSqlResult parse(String sqlBeforeParse, Method method, Object[] args) throws Exception {

        if (args == null || args.length == 0) return new ParseSqlResult(sqlBeforeParse);

        return INSTANCE.parserSqlWithParameters(sqlBeforeParse, method, args);
    }

    private ParseSqlResult parserSqlWithParameters(String sqlBeforeParse, Method method, Object[] args) throws Exception {

        Parameter[] parameters = method.getParameters();

        if (parameters == null || parameters.length == 0) return new ParseSqlResult(sqlBeforeParse);

        ArrayMap<String, Integer> positionMap = paramPositionMap(parameters);
        List<ParameterRange> rangeList = paramInSqlPosition(sqlBeforeParse);

        return replaceSqlWithParameters(sqlBeforeParse, rangeList, positionMap, args);

    }

    private ParseSqlResult replaceSqlWithParameters(String sqlBeforeParse, List<ParameterRange> rangeList, ArrayMap<String, Integer> positionMap, Object[] args) {

        String sqlAfterParse = sqlBeforeParse;
        List<Object> values = new LinkedList<>();

        for (ParameterRange range : rangeList) {
            String holder = "#{" + range.getValue() + "}";
            sqlAfterParse = sqlAfterParse.replace(holder, "?");
            values.add(args[positionMap.get(range.getValue())]);
        }

        return new ParseSqlResult(sqlAfterParse, values);

    }

    private ArrayMap<String, Integer> paramPositionMap(Parameter[] parameters) {

        ArrayMap<String, Integer> positionMap = new ArrayMap<>(parameters.length);

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Param param = parameter.getAnnotation(Param.class);

            if (param == null) continue;

            positionMap.put(param.value(), i);
        }

        return positionMap;
    }

    private List<ParameterRange> paramInSqlPosition(String sqlBeforeParse) throws Exception {

        List<ParameterRange> list = new LinkedList<>();

        char[] sql = sqlBeforeParse.toCharArray();

        int start = 0;
        int end = 0;

        while (start < sql.length) {

            while (end < sql.length && sql[end] != '}') end++;
            while (start < sql.length && sql[start] != '#') start++;

            if (end == sql.length || start == sql.length) break;

            if (sql[start] == '#' && sql[end] == '}') {

                if (sql[start + 1] != '{') throw new Exception("SQL参数配置错误");

                ParameterRange range = new ParameterRange();
                range.setStart(start);
                range.setEnd(end);
                range.setValue(sqlBeforeParse.substring(start + 2, end));
                list.add(range);

                end++;
                start = end;

            }

        }

        return list;
    }

    public static void main(String[] args) throws Exception {
        String sql = "select * from user where name = #{name} and age = #{age} and name = #{name}";
    }


}
