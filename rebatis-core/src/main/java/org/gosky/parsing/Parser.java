package org.gosky.parsing;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.gosky.util.TypeUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: Galaxy
 * @date: 2019-05-26 15:10
 **/
public class Parser {

    private static final Pattern PARAM_PATTERN = Pattern.compile("(?<!\\\\)#\\{(.*)}");

    private static final Parser INSTANCE = new Parser();

    public static ParseSqlResult parse(String sqlBeforeParse, Method method, Object[] args) {

        if (args == null || args.length == 0) return new ParseSqlResult(sqlBeforeParse);

        return INSTANCE.parserSqlWithParameters(sqlBeforeParse, method, args);
    }

    private ParseSqlResult parserSqlWithParameters(String sqlBeforeParse, Method method, Object[] args) {

        Parameter[] parameters = method.getParameters();

        if (parameters == null || parameters.length == 0) return new ParseSqlResult(sqlBeforeParse);

        //解析方法参数
        ParamNameResolver paramNameResolver = new ParamNameResolver(method);
        Object parameter = paramNameResolver.getNamedParams(args);
        //parameterMappings 如果多参数那么是个map,如果单参数且不是list/array 就是原对象
        //sql中的#{name} 的list
        List<String> mapping = new ArrayList<>();
        GenericTokenParser parser = new GenericTokenParser("#{", "}", content -> {
            mapping.add(content);
            return "?";
        });
        //往mapping中添加值
        String sql1 = parser.parse(sqlBeforeParse);
        Map<String, Object> paramMapping = new IdentityHashMap<>();
        for (String name : mapping) {
            if (TypeUtil.typeList.contains(parameter.getClass())) {
                //单参数java基础类型
                paramMapping.put(name, parameter);
            } else {
                //pojo或者map
                Object value = MetaObject.forObject(parameter).getValue(name);
                paramMapping.put(name, value);
            }
        }

        //处理掉为空的where条件
        SQLStatement sqlStatement = SQLUtils.parseStatements(sqlBeforeParse, JdbcConstants.MYSQL).get(0);

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
                                }
                            }
                        }
                    }
                }

                GenericTokenParser parser2 = new GenericTokenParser("#{", "}", content -> "?");
                String sql2 = parser2.parse(SQLUtils.toMySqlString(query,
                        new SQLUtils.FormatOption(false, false)));
                return new ParseSqlResult(sql2,
                        new ArrayList<>(paramMapping.values()));
            }
        }

        return new ParseSqlResult(sql1, new ArrayList<>(paramMapping.values()));

    }

}
