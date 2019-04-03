package org.gosky.parsing;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gosky.mapping.MapperHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * SQL解析处理
 *
 * @author zhiqin.zhang
 */
@Slf4j
public class SQLParsingHandler {


    /**
     * 解析SQL
     *
     * @return
     */
    public String parsingSQL(String sql) {
        if (StringUtils.isBlank(sql)) {
            log.error("sql is null");
            throw new NullPointerException("sql is null");
        }
        if (!isValid(sql)) {
            throw new IllegalArgumentException("sql error format");
        }
        StringBuilder stringBuilder = new StringBuilder(sql);
        while (stringBuilder.indexOf("#") > 0) {
            int open = stringBuilder.indexOf("{");
            int close = stringBuilder.indexOf("}");
            stringBuilder.replace(open - 1, close + 1, "hello");
            System.out.println(stringBuilder);
        }
        return stringBuilder.toString();
    }

    public static void main(String[] args) {
        new MapperHandler().parsingInterface("org.gosky.mapping");
        new SQLParsingHandler().parsingSQL("update user set name = #name} where id =#{id};");

    }

    /**
     * 检查sql {} 格式
     *
     * @param s
     * @return
     */
    private boolean isValid(String s) {
        if (StringUtils.isBlank(s)) {
            return false;
        }
        Map<Character, Character> map = new HashMap<>(1);
        map.put('}', '{');
        StringBuilder vaildSb = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '{' || c == '}') {
                vaildSb.append(c);
            }
        }
        if (vaildSb.length() == 0) {
            return true;
        }
        char[] chars = vaildSb.toString().toCharArray();
        Stack<Character> stringStack = new Stack<>();
        for (char c : chars) {
            if (map.get(c) == null) {
                stringStack.push(c);
            } else if (stringStack.size() > 0 && map.get(c).equals(stringStack.peek())) {
                stringStack.pop();
            } else {
                return false;
            }
        }
        return stringStack.size() == 0;
    }
}
