package org.gosky.util

import com.alibaba.ttl.TransmittableThreadLocal
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.SqlClient
import org.gosky.Rebatis
import org.slf4j.LoggerFactory

object TransactionUtil {
    private val logger = LoggerFactory.getLogger(TransactionUtil::class.java)
    val context = TransmittableThreadLocal<SqlClient>()

    suspend fun inTransaction(call: Call) {
        val connection = Rebatis.rebatis.executor.gettConnectionPool().connection.await()
        val transaction = connection.begin().await()
        val result = kotlin.runCatching {
            call.invoke()
        }
        if (result.isSuccess) {
            transaction.commit()
        } else {
            logger.warn("transaction rollback", result.exceptionOrNull())
            transaction.rollback()
        }
        connection.close()
    }

    interface Call {
        suspend fun invoke()
    }
}