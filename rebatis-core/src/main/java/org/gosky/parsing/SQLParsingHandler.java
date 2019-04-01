package org.gosky.parsing;

import org.gosky.mapping.MapperHandler;

/**
 * SQL解析处理
 * @author zhiqin.zhang
 */
public class SQLParsingHandler {


    public void parsingSQL() {
        String sql = "update user set name = #{name} where id =#{id}";
        StringBuilder stringBuilder = new StringBuilder(sql);
        while (stringBuilder.indexOf("#") > 0) {
            int open = stringBuilder.indexOf("{");
            int close = stringBuilder.indexOf("}");
            stringBuilder.replace(open - 1, close + 1, "hello");
            System.out.println(stringBuilder);
        }
    }

    public static void main(String[] args) {
        new MapperHandler().parsingInterface("org.gosky.mapping");
        new SQLParsingHandler().parsingSQL();

    }
}
