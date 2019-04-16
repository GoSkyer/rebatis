package org.gosky.mapping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gosky.common.ReturnTypeEnum;
import org.gosky.common.SQLType;

/**
 * 具体方法映射
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MethodMapper {
    private String methodName;
    private String sql;
    private SQLType sqlType;
    private Class<?> returnType;
    private Class<?>[] parameterTypes;
    private ReturnTypeEnum returnTypeEnum;
}
