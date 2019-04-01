package org.gosky;

import com.github.jasync.sql.db.pool.ConnectionPool;

import org.gosky.executor.Executor;
import org.gosky.executor.SimpleExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Auther: guozhong
 * @Date: 2019-03-11 23:11
 * @Description:
 */
public class Rebatis {

    private final Map<Method, ServiceMethod<?>> serviceMethodCache = new ConcurrentHashMap<>();
    private final Executor executor;

    Rebatis(Executor executor) {
        this.executor = executor;
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
                        // TODO: 2019-03-11 单独的mapperService 用于保存sql语句 adapter 等等
                        return executor.query((String) args[0], "");
                    }
                });
    }

//    ServiceMethod<?> loadServiceMethod(Method method) {
//        ServiceMethod<?> result = serviceMethodCache.get(method);
//        if (result != null) return result;
//
//        synchronized (serviceMethodCache) {
//            result = serviceMethodCache.get(method);
//            if (result == null) {
//                result = ServiceMethod.parseAnnotations(this, method);
//                serviceMethodCache.put(method, result);
//            }
//        }
//        return result;
//    }


    public static final class Builder {
        private final ConnectionPool connectionPool;

        public Builder(ConnectionPool connectionPool) {
            this.connectionPool = connectionPool;
        }

        public Rebatis build() {
            return new Rebatis(new SimpleExecutor(connectionPool));
        }
    }
}
