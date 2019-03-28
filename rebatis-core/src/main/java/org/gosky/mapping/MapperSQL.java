package org.gosky.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 接口SQL映射
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MapperSQL {
    private String name;
    private Class<?> clazz;
    Map<String, String> methodSQL;
    private List<String> sqlList;
    private List<Method> methodList;
    private String[] value;
}
