package org.gosky.mapping;



import java.util.List;
import java.util.Map;

/**
 * 接口映射
 */

public class MappedStatement {
    private String interfaceName;
    private String mapperName;
    private Class<?> clazz;
    Map<String, String> methodSQL;
    private String[] value;
    private List<SqlFactory> list;
}
