package org.gosky;

import com.github.jasync.sql.db.mysql.MySQLConnection;
import com.github.jasync.sql.db.pool.ConnectionPool;

import org.gosky.adapter.CallAdapter;
import org.gosky.adapter.DefaultCallAdapterFactory;
import org.gosky.converter.ConverterFactory;
import org.gosky.executor.Executor;
import org.gosky.executor.SimpleExecutor;
import org.gosky.mapping.ServiceMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.unmodifiableList;
import static org.gosky.util.Utils.checkNotNull;

@Slf4j
public class Rebatis {

    public final Executor executor;
    private final Map<Method, ServiceMethod> serviceMethodCache = new ConcurrentHashMap<>();
    public final ConverterFactory converterFactory;
    public final List<CallAdapter.Factory> callAdapterFactories;

    private Rebatis(Executor executor, ConverterFactory converterFactory, List<CallAdapter.Factory> callAdapterFactories) {
        this.executor = executor;
        this.converterFactory = converterFactory;
        this.callAdapterFactories = callAdapterFactories;
    }

    @SuppressWarnings("unchecked") // Single-interface proxy creation guarded by parameter safety.
    public <T> T create(final Class<T> mapper) {
        return (T) Proxy.newProxyInstance(mapper.getClassLoader(), new Class<?>[]{mapper},
                new InvocationHandler() {

                    private final Object[] emptyArgs = new Object[0];

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        // If the method is a method from Object then defer to normal invocation.
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }

                        return loadServiceMethod(method).invoke(args);
                    }
                });
    }

    private ServiceMethod loadServiceMethod(Method method) {
        ServiceMethod result = serviceMethodCache.get(method);
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


    public CallAdapter<?, ?> callAdapter(Type returnType, Annotation[] annotations) {
        checkNotNull(returnType, "returnType == null");
        checkNotNull(annotations, "annotations == null");

        for (CallAdapter.Factory callAdapterFactory : callAdapterFactories) {
            CallAdapter<?, ?> adapter = callAdapterFactory.get(returnType, annotations);
            if (adapter != null) {
                return adapter;
            }
        }

        StringBuilder builder = new StringBuilder("Could not locate call adapter for ")
                .append(returnType)
                .append(".\n");

        builder.append("  Tried:");

        for (CallAdapter.Factory callAdapterFactory : callAdapterFactories) {
            builder.append("\n   * ").append(callAdapterFactory.getClass().getName());
        }

        throw new IllegalArgumentException(builder.toString());
    }


    public static final class Builder {
        private ConnectionPool<MySQLConnection> connectionPool;
        private ConverterFactory converterFactor;
        private final List<CallAdapter.Factory> callAdapterFactories = new ArrayList<>();

        public Builder connectionPool(ConnectionPool<MySQLConnection> connectionPool) {
            checkNotNull(connectionPool, "connectionPool == null");
            this.connectionPool = connectionPool;
            return this;
        }

        public Builder converterFactory(ConverterFactory converterFactory) {
            this.converterFactor = converterFactory;
            return this;
        }


        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            callAdapterFactories.add(checkNotNull(factory, "factory == null"));
            return this;
        }

        public Rebatis build() {

            if (connectionPool == null) {
                throw new IllegalStateException("connectionPool required.");
            }

            if (converterFactor == null) {
                try {
                    this.converterFactor = (ConverterFactory) Class.forName("org.gosky.rebatis.apt.RebatisConverterFactory").newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalStateException("converterFactor required.");
                }
            }

            if (callAdapterFactories.size() == 0) {
                log.debug("callAdapterFactories size = 0, add default call");
                callAdapterFactories.add(new DefaultCallAdapterFactory());
            }

            return new Rebatis(new SimpleExecutor(connectionPool), converterFactor, unmodifiableList(callAdapterFactories));
        }
    }
}
