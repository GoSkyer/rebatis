package org.gosky.util;


import com.alibaba.ttl.TransmittableThreadLocal;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;
import org.gosky.Rebatis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class TransactionUtil {

    private static final Logger logger = LoggerFactory.getLogger(TransactionUtil.class);
    public static TransmittableThreadLocal<SqlClient> context = new TransmittableThreadLocal<>();

    public static void start(Call call) {
        Rebatis.rebatis.executor.gettConnectionPool().withTransaction(sqlClient -> {
            try {
                context.set(sqlClient);
                call.invoke();
                context.remove();
                return Future.succeededFuture();
            } catch (Exception e) {
//                e.printStackTrace();
                logger.warn("transaction rollback", e);
                return Future.failedFuture(e);
            }
        });
    }

    public interface Call {
        void invoke() throws InterruptedException, ExecutionException;
    }


}
