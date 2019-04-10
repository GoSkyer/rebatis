package org.gosky.rebatis.sample;

import com.github.jasync.sql.db.QueryResult;

import org.gosky.converter.Converter;
import org.gosky.converter.ConverterFactory;

import java.util.HashMap;

/**
 * @Auther: guozhong
 * @Date: 2019-04-02 17:06
 * @Description:
 */
public class TestConverterFactory implements ConverterFactory {

    private HashMap<Class, Converter> map = new HashMap<>();

    @Override
    public void init() {
        Converter testConverter = new TestConverter();
        map.put(testConverter.getEntityClass(), testConverter);
    }

    @Override
    public Object convert(QueryResult qr, Class pojoClass) {
        return map.get(pojoClass).convert(qr);
    }
}
