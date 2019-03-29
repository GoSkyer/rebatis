package org.gosky.parsing;

/**
 * SQL解析处理
 */
public class SQLParsingHandler {


    public void parsingSQL() {
        String sql = "update set name  #{name} where id =#{id}";
        int i = sql.indexOf("#");
        int i1 = sql.indexOf("}");
        System.out.println(sql.substring(i + 1, i1));
        StringBuilder stringBuilder = new StringBuilder(sql);
        String substring = stringBuilder.substring(i, i1 + 1);
        StringBuilder hello = stringBuilder.replace(i, i1 + 1, "hello");
        System.out.println(substring);
        System.out.println(hello);

    }

    public static void main(String[] args) {
        new SQLParsingHandler().parsingSQL();

    }
}
