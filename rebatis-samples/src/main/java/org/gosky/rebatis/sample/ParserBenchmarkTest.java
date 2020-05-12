package org.gosky.rebatis.sample;

import com.github.jasync.sql.db.ConnectionPoolConfigurationBuilder;
import com.github.jasync.sql.db.mysql.MySQLConnection;
import com.github.jasync.sql.db.mysql.MySQLConnectionBuilder;
import com.github.jasync.sql.db.pool.ConnectionPool;

import io.reactivex.functions.Consumer;
import org.gosky.Rebatis;
import org.gosky.rebatis.sample.mapper.TestMapper;

import java.util.List;
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

        ConnectionPool<MySQLConnection> connectionPool = MySQLConnectionBuilder.createConnectionPool(cfg -> {
            cfg.setUsername("root");
            cfg.setPassword("root");
            cfg.setHost("localhost");
            cfg.setPort(3306);
            cfg.setDatabase("test");
            cfg.setMaxActiveConnections(100);
            cfg.setMaxIdleTime(TimeUnit.MINUTES.toMillis(15));
            cfg.setMaxPendingQueries(10_000);
            cfg.setConnectionValidationInterval(TimeUnit.SECONDS.toMillis(30));
            return Unit.INSTANCE;
        });

        Rebatis rebatis = new Rebatis.Builder().connectionPool(connectionPool).build();
//        rebatis.create(TestMapper.class).test(18, "Tom").subscribe(System.out::println);

        while (true) {

        }

    }
}
