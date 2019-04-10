package org.gosky.rebatis.sample;

import com.github.jasync.sql.db.QueryResult;

import org.gosky.converter.Converter;

/**
 * @Auther: guozhong
 * @Date: 2019-04-02 17:07
 * @Description:
 */
public class TestConverter implements Converter {

    @Override
    public User convert(QueryResult qr) {
        User user = new User();
        return user;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Class getEntityClass() {
        return null;
    }
}
