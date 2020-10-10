package org.gosky.parsing;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import jdk.nashorn.internal.ir.LiteralNode;
import kotlin.Pair;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: Galaxy
 * @date: 2019-05-26 15:10
 **/
public class Parser {

    private static final Pattern PARAM_PATTERN = Pattern.compile("(?<!\\\\)#\\{(.*)}");

    private static final Parser INSTANCE = new Parser();

    public static ParseSqlResult parse(String sqlBeforeParse, Method method, Object[] args, boolean isSimpleType, Object parameter, MetaObject metaObject) {

//        if (args == null || args.length == 0) return new ParseSqlResult(sqlBeforeParse);

        return INSTANCE.parserSqlWithParameters(sqlBeforeParse, method, args, isSimpleType, parameter, metaObject);
    }

    private ParseSqlResult parserSqlWithParameters(String sqlBeforeParse, Method method, Object[] args, boolean isSimpleType, Object parameter, MetaObject metaObject) {

        Parameter[] parameters = method.getParameters();

        if (parameters == null || parameters.length == 0) return new ParseSqlResult(sqlBeforeParse);

        //parameterMappings 如果多参数那么是个map,如果单参数且不是list/array 就是原对象
        //sql中的#{name} 的list
        List<String> mapping = new ArrayList<>();
        GenericTokenParser parser = new GenericTokenParser("#{", "}", content -> {
            mapping.add(content);
            return "?";
        });
        //往mapping中添加值(parser的时候,匿名callback才会被调用)
        parser.parse(sqlBeforeParse);

        List<Pair<String, Object>> paramMappingList = new ArrayList<>();
        Map<String, Object> paramMapping = new LinkedHashMap<>();
        for (String name : mapping) {
            if (isSimpleType) {
                //是否是单参数java基础类型
                paramMapping.put(name, parameter);
                paramMappingList.add(new Pair<>(name, parameter));
            } else {
                //pojo或者map
                Object value = metaObject.getValue(name);
                paramMapping.put(name, value);
                paramMappingList.add(new Pair<>(name, value));
            }
        }



        //处理掉为空的where条件
        SQLStatement sqlStatement = SQLUtils.parseStatements(sqlBeforeParse, JdbcConstants.MYSQL).get(0);
        List<Object> paramValues = null;
        if (sqlStatement instanceof SQLSelectStatement) {
            //只处理是查询的
            SQLSelectQuery query = ((SQLSelectStatement) sqlStatement).getSelect().getQuery();
            if (query instanceof SQLSelectQueryBlock) {
                SQLExpr where = ((SQLSelectQueryBlock) query).getWhere();
                if (where instanceof SQLBinaryOpExpr) {
                    for (SQLExpr sqlExpr : SQLBinaryOpExpr.split((SQLBinaryOpExpr) where)) {
                        if (sqlExpr instanceof SQLBinaryOpExpr) {
                            String right = ((SQLBinaryOpExpr) sqlExpr).getRight().toString();
                            Matcher matcher = PARAM_PATTERN.matcher(right);
                            if (matcher.find()) {
                                right = matcher.group(1);
                                Object value = paramMapping.get(right);
                                if (paramMapping.containsKey(right) && value == null) {
                                    ((SQLSelectQueryBlock) query).removeCondition((sqlExpr));
                                    paramMapping.remove(right);
                                    String finalRight = right;
                                    paramMappingList = paramMappingList.stream().filter(t -> !t.getFirst().equals(finalRight)).collect(Collectors.toList());
                                }
                            }
                        }
                    }
                }

                GenericTokenParser parser2 = new GenericTokenParser("#{", "}", content -> "?");
                sqlBeforeParse = SQLUtils.toMySqlString(query, new SQLUtils.FormatOption(false, false));
                parser2.parse(sqlBeforeParse);
                paramValues = paramMappingList.stream().map(Pair::getSecond).collect(Collectors.toList());
            }
        } else {
            paramValues = new ArrayList<>(paramMapping.values());
        }

        //处理为数组的参数
        GenericTokenParser parser3 = new GenericTokenParser("#{", "}", content -> {
            Object o = paramMapping.get(content);
            if ((o instanceof Collection)) {
                Object[] collect = ((Collection) o).stream().map(o1 -> "?").toArray();
                return StringUtils.join(collect,",");
            } else {
                return "?";
            }
        });
        String sql = parser3.parse(sqlBeforeParse);
        List paramValues2 = new ArrayList();
        for (Object value : paramValues) {
            if (value instanceof Collection){
                paramValues2.addAll((Collection) value);
            } else {
                paramValues2.add(value);
            }
        }

        return new ParseSqlResult(sql, paramValues2);
    }

}
