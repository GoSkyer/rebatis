package org.gosky.mapping;

import org.gosky.common.ReturnTypeEnum;
import org.gosky.common.SQLType;

import java.lang.reflect.Type;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 具体方法映射
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SqlFactory {
    private String methodName;
    private String sql;
    private SQLType sqlType;
    private Type returnType;
//    private Type adapterType;
    private Type responseType;
    private Class<?>[] parameterTypes;
    private ReturnTypeEnum returnTypeEnum;
}
