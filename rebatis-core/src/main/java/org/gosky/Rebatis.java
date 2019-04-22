package org.gosky;

import com.github.jasync.sql.db.pool.ConnectionPool;

import org.gosky.converter.ConverterFactory;
import org.gosky.executor.Executor;
import org.gosky.executor.SimpleExecutor;
import org.gosky.mapping.ServiceMethod;

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

    public final Executor executor;
    private final Map<Method, ServiceMethod<?>> serviceMethodCache = new ConcurrentHashMap<>();

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
                        return loadServiceMethod(method).invoke(args);
                    }
                });
    }

    ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod<?> result = serviceMethodCache.get(method);
        if (result != null) return result;

        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = ServiceMethod.parseAnnotations(this, method);
                serviceMethodCache.put(method, result);
            }
        }
        return result;
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
