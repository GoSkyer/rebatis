package org.gosky.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 接口映射
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MapperSQL {
    private String interfaceName;
    private Class<?> clazz;
    Map<String, String> methodSQL;
    private String[] value;
    private List<SqlFactory> list;

}
