package org.gosky;

import com.github.jasync.sql.db.pool.ConnectionPool;

import org.gosky.converter.ConverterFactory;
import org.gosky.executor.Executor;
import org.gosky.executor.SimpleExecutor;
import org.gosky.mapping.MapperHandler;
import org.gosky.mapping.ServiceMethod;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * @Auther: guozhong
 * @Date: 2019-03-11 23:11
 * @Description:
 */
public class Rebatis {

    public final Executor executor;
    private MapperHandler mapperHandler = new MapperHandler();
    public final ConverterFactory converterFactory;

    Rebatis(Executor executor, ConverterFactory converterFactory) {
        this.executor = executor;
        this.converterFactory = converterFactory;
    }

    @SuppressWarnings("unchecked") // Single-interface proxy creation guarded by parameter safety.
    public <T> T create(final Class<T> mapper) {
        return (T) Proxy.newProxyInstance(mapper.getClassLoader(), new Class<?>[]{mapper},
                new InvocationHandler() {
                    private final Object[] emptyArgs = new Object[0];

                    @Override
                    public Object invoke(Object proxy, Method method,
                                         Object[] args) throws Throwable {
                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
//                        if (platform.isDefaultMethod(method)) {
//                            return platform.invokeDefaultMethod(method, mapper, proxy, args);
//                        }
                        // TODO: 2019-03-11 java8 默认方法

//                        return loadServiceMethod(method).invoke(args != null ? args : emptyArgs);
                        ServiceMethod sqlFactory = loadServiceMethod(method);

//                        CompletableFuture<QueryResult> query = executor.query(sqlFactory.getSql(), "");
//
//                        PreConverter preConverter = new PreConverter();
//
//                        CompletableFuture<Object> objectCompletableFuture = query.thenApply(queryResult -> {
//                            try {
//                                return preConverter.with(converterFactory).convert(queryResult
//                                        , Utils.getParameterUpperBound(0, ((ParameterizedType) sqlFactory.getReturnType())));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            return null;
//                        });

                        Object invoke = sqlFactory.invoke(args);

                        return invoke;
                    }
                });
    }

    ServiceMethod loadServiceMethod(Method method) {
        return ServiceMethod.parseAnnotations(this, method);
    }


    public static final class Builder {
        private final ConnectionPool connectionPool;
        private ConverterFactory converterFactor;

        public Builder(ConnectionPool connectionPool) {
            this.connectionPool = connectionPool;
        }

        public Builder setConverterFactory(ConverterFactory converterFactory) {
            this.converterFactor = converterFactory;
            return this;
        }

        public Rebatis build() {
            return new Rebatis(new SimpleExecutor(connectionPool), converterFactor);
        }
    }
}
