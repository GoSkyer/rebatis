package org.gosky.rebatis.sample;


import com.github.jasync.sql.db.ConnectionPoolConfigurationBuilder;
import com.github.jasync.sql.db.mysql.MySQLConnection;
import com.github.jasync.sql.db.mysql.MySQLConnectionBuilder;
import com.github.jasync.sql.db.pool.ConnectionPool;

import org.gosky.Rebatis;
import org.gosky.rebatis.apt.RebatisConverterFactory;
import org.gosky.rebatis.sample.mapper.TestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


/**
 * @Auther: guozhong
 * @Date: 2019-03-10 23:42
 * @Description:
 */
public class Main {
    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        ConnectionPool<MySQLConnection> connectionPool = MySQLConnectionBuilder.createConnectionPool(new Function1<ConnectionPoolConfigurationBuilder, Unit>() {
            @Override
            public Unit invoke(ConnectionPoolConfigurationBuilder t) {
                t.setUsername("root");
                t.setHost("localhost");
                t.setPort(3306);
                t.setPassword("123456");
                t.setDatabase("test");
                t.setMaxActiveConnections(100);
                t.setMaxIdleTime(TimeUnit.MINUTES.toMillis(15));
                t.setMaxPendingQueries(10_000);
                t.setConnectionValidationInterval(TimeUnit.SECONDS.toMillis(30));
                return Unit.INSTANCE;
            }
        });
        RebatisConverterFactory rebatisConverterFactory = new RebatisConverterFactory();

        Rebatis rebatis = new Rebatis.Builder(connectionPool)
                .setConverterFactory(rebatisConverterFactory)
                .build();
        rebatis.create(TestMapper.class).test()
                .thenAccept(queryResult -> {
                    System.out.println("queryResult : " + queryResult.toString());
//                    PreConverter preConverter = new PreConverter();
//                    try {
//                        TypeToken listTypeToken = new TypeToken<List<User>>(){};
//                        List<User> user = (List<User>) preConverter.with(rebatisConverterFactory).convert(queryResult, listTypeToken.getType());
////                    LIST<User> user = (LIST<User>) rebatisConverterFactory.convert(queryResult, User.class);
//                        System.out.println("pojo" + user);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                });

        while (true) {

        }
    }
}
