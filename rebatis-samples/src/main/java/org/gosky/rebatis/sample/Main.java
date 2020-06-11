package org.gosky.rebatis.sample;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import org.gosky.Rebatis;
import org.gosky.adapter.rxjava2.RxJava2CallAdapterFactory;
import org.gosky.rebatis.apt.RebatisConverterFactory;
import org.gosky.rebatis.sample.mapper.TestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @Auther: guozhong
 * @Date: 2019-03-10 23:42
 * @Description:
 */
public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
                .setPort(3306)
                .setHost("the-host")
                .setDatabase("the-db")
                .setUser("user")
                .setPassword("secret");

        // Pool options
        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);

        // Create the client pool
        MySQLPool client = MySQLPool.pool(connectOptions, poolOptions);


        RebatisConverterFactory rebatisConverterFactory = new RebatisConverterFactory();

        Rebatis rebatis = new Rebatis.Builder()
                .connectionPool(client)
//                .converterFactory()
                .addCallAdapterFactory(new RxJava2CallAdapterFactory())
                .build();

        TestMapper testMapper = rebatis.create(TestMapper.class);

        User user = new User();
//        user.setName("zhangsan");
        user.setAge(10);
        testMapper.test(user, 10)
                .subscribe(users -> {
                    System.out.println(users);
                }, throwable -> {
                    throwable.printStackTrace();
                });


        final String dbType = JdbcConstants.MYSQL; // 可以是ORACLE、POSTGRESQL、SQLSERVER、ODPS等

        String sql = "select * from t where name = #{name} and age = #{age}";

        SQLSelectStatement sqlStatement = (SQLSelectStatement) SQLUtils.parseStatements(sql, dbType).get(0);
//
//        SQLSelectQuery sqlSelectQuery = sqlStatement.getSelect().getQuery();
//        SQLSelectQueryBlock sqlSelectQueryBlock = (SQLSelectQueryBlock) sqlSelectQuery;
//        SQLExpr where = sqlSelectQueryBlock.getWhere();
//        System.out.println(where);
//        List<SQLObject> mergedList = ((SQLBinaryOpExpr) where).getChildren();
//        SQLExpr sqlObject = (SQLExpr)  mergedList.get(0);
//        System.out.println(sqlObject.toString());
//        sqlSelectQueryBlock.removeCondition(sqlObject);
//        System.out.println(sqlSelectQueryBlock);

//        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
//        sqlStatement.accept(visitor);
//        System.out.println(visitor.getConditions());
//


//        // Create the client pool
//        MySQLPool client = MySQLPool.pool(connectOptions, poolOptions);
//
//        // A simple query
//        client
//                .preparedQuery("SELECT * FROM users WHERE id='julien'")
//                .execute(ar -> {
//                    if (ar.succeeded()) {
//                        ar.ma
//                        RowSet<Row> result = ar.result();
//                        System.out.println("Got " + result.size() + " rows ");
//                    } else {
//                        System.out.println("Failure: " + ar.cause().getMessage());
//                    }
//                });
//
//        SqlTemplate.forQuery()
//                .mapTo()
//                .execute();

        while (true) {

        }
    }
}
