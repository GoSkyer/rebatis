package org.gosky.common;


import java.util.Arrays;

/**
 * SQL语句类型
 */
public enum SQLType {

    UPDATE("update"),
    INSERT("insert"),
    DELETE("delete"),
    SELECT("select");


    SQLType(String type) {
        this.type = type;
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static SQLType covertToSQLType(String typeParm) {
        SQLType sqlType = Arrays.stream(SQLType.values()).filter(t -> t.type.equals(typeParm))
                .findFirst().orElse(null);
        if (sqlType == null) {
            throw new RuntimeException("Don't have this sql type ,please check ");
        }
        return sqlType;
    }
}
