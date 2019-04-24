package org.gosky.rebatis.sample;

import com.github.jasync.sql.db.ConnectionPoolConfigurationBuilder;
import com.github.jasync.sql.db.mysql.MySQLConnection;
import com.github.jasync.sql.db.mysql.MySQLConnectionBuilder;
import com.github.jasync.sql.db.pool.ConnectionPool;

import org.gosky.Rebatis;
import org.gosky.rebatis.apt.RebatisConverterFactory;
import org.gosky.rebatis.sample.mapper.TestMapper;

import java.util.concurrent.TimeUnit;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * @Auther: guozhong
 * @Date: 2019-04-14 15:17
 * @Description:
 */
public class ParserBenchmarkTest {

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
        rebatisConverterFactory.init();

        Rebatis rebatis = new Rebatis.Builder().connectionPool(connectionPool).build();
        rebatis.create(TestMapper.class).test();

        while (true) {

        }
    }
}
