package org.gosky;


import org.gosky.executor.Executor;
import org.gosky.executor.SimpleExecutor;
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
        Executor executor = new SimpleExecutor();
        executor.query("select * from user", null).thenAccept(queryResult -> {
            System.out.println("hahahh");
            System.out.println(queryResult.toString());
        });

        while (true) {

        }
    }
}
