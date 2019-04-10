package org.gosky.converter;

import com.github.jasync.sql.db.QueryResult;

/**
 * @Auther: guozhong
 * @Date: 2019-04-02 17:03
 * @Description:
 */
public interface ConverterFactory {

    void init();

    Object convert(QueryResult qr, Class pojoClass);


}
