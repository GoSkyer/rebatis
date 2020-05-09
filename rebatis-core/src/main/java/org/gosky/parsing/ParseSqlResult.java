package org.gosky.parsing;

import java.util.List;

/**
 * @author: Galaxy
 * @date: 2019-05-26 22:08
 **/
public class ParseSqlResult {

    private String sql;
    private List<Object> values;

    public ParseSqlResult(String sql) {
        this.sql = sql;
    }

    public ParseSqlResult(String sql, List<Object> values) {
        this.sql = sql;
        this.values = values;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Object> getValues() {
        return values;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "ParseSqlResult{" +
                "sql='" + sql + '\'' +
                ", values=" + values +
                '}';
    }

}
