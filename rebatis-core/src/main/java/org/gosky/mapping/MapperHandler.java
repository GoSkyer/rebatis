package org.gosky.mapping;

import org.gosky.annotations.Delete;
import org.gosky.annotations.Insert;
import org.gosky.annotations.Select;
import org.gosky.annotations.Update;
import org.gosky.common.ReturnTypeEnum;
import org.gosky.common.SQLType;
import org.gosky.util.ClassHelper;
import org.gosky.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * mapper映射解析
 */
@Slf4j
public class MapperHandler {


    private final Map<Method, ServiceMethod> methodMapperList = new HashMap<>();
    private String packageName;

    public MapperHandler() {
        parsingInterface("org.gosky.rebatis.sample.mapper");
    }


    public Map<Method, ServiceMethod> getMethodMapperList() {
        return methodMapperList;
    }

    /**
     * 注解解析功能
     * TODO 语句解析 and 参数解析
     * TODO 返回值处理(包括泛型)
     *
     * @param packageName
     */
    public void parsingInterface(String packageName) {
        log.info("handle annotations start ....");
        Set<Class<?>> mapperInterfaceSet = ClassHelper.getInstance(packageName).getMapperInterfaceSet();
        if (!CollectionUtils.isEmpty(mapperInterfaceSet)) {
            mapperInterfaceSet.forEach(cls -> {
                System.out.println(cls.getName());
                Method[] methods = cls.getMethods();
                if (methods.length > 0) {
                    Arrays.stream(methods).forEach(method -> {
                        Annotation[] annotations = method.getDeclaredAnnotations();
                        String simpleName = annotations[0].annotationType().getSimpleName();
                        //获取SQL类型
                        SQLType sqlType = SQLType.covertToSQLType(simpleName);
                        String[] value = {};
                        switch (sqlType) {
                            case INSERT:
                                Insert inserts = (Insert) annotations[0];
                                value = inserts.value();
                                break;
                            case DELETE:
                                Delete delete = (Delete) annotations[0];
                                value = delete.value();
                                break;
                            case UPDATE:
                                Update update = (Update) annotations[0];
                                value = update.value();
                                break;
                            case SELECT:
                                Select select = (Select) annotations[0];
                                value = select.value();
                                break;
                            default:
                                break;
                        }

                        ReturnTypeEnum returnTypeEnum;
                        switch (method.getReturnType().getSimpleName()) {
                            case "void":
                                returnTypeEnum = ReturnTypeEnum.VOID;
                                break;
                            case "java.util.List":
                                returnTypeEnum = ReturnTypeEnum.LIST;
                                break;
                            case "java.util.Map":
                                returnTypeEnum = ReturnTypeEnum.MAP;
                                break;
                            default:
                                returnTypeEnum = ReturnTypeEnum.SINGLE;
                                break;
                        }
                        //构建方法SQL映射
                        methodMapperList.put(method, ServiceMethod.builder().methodName(method.getName())
                                .returnType(method.getGenericReturnType())
                                .returnTypeEnum(returnTypeEnum)
                                .parameterTypes(method.getParameterTypes())
                                .sql(value[0])
                                .sqlType(sqlType)
                                .build());
                    });
                }
            });
        }
        log.info("handle annotations end ....");
    }
}