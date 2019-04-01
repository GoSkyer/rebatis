package org.gosky.mapping;

import lombok.extern.slf4j.Slf4j;
import org.gosky.annotations.Delete;
import org.gosky.annotations.Insert;
import org.gosky.annotations.Select;
import org.gosky.annotations.Update;
import org.gosky.common.SQLType;
import org.gosky.mapping.MapperSQL;
import org.gosky.mapping.MethodMapper;
import org.gosky.util.ClassHelper;
import org.gosky.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * mapper映射解析
 */
@Slf4j
public class MapperHandler {


    public static List<MethodMapper> methodMapperList;
    public static Map<Class<?>, List<MethodMapper>> sqlMapper;

    /**
     * 注解解析功能
     * TODO 语句解析 and 参数解析
     *
     * @param packageName
     */
    public void parsingInterface(String packageName) {
        log.info("handle annotations start ....");
        Set<Class<?>> mapperInterfaceSet = ClassHelper.getMapperInterfaceSet();
        if (!CollectionUtils.isEmpty(mapperInterfaceSet)) {
            sqlMapper = new HashMap<>(mapperInterfaceSet.size());
            methodMapperList = new ArrayList<>();
            mapperInterfaceSet.forEach(cls -> {
                System.out.println(cls.getName());
                sqlMapper.put(cls, methodMapperList);
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
                        //构建方法SQL映射
                        methodMapperList.add(MethodMapper.builder().methodName(method.getName())
                                .returnType(method.getReturnType())
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