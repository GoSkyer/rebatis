package org.gosky.rebatis.sample;

import com.github.jasync.sql.db.QueryResult;

import org.gosky.mapping.ResultSetMapper;

/**
 * @Auther: guozhong
 * @Date: 2019-04-01 15:09
 * @Description:
 */
public class Test implements ResultSetMapper {
    @Override
    public <T> T parser(QueryResult qr,T t) {
        return null;
    }
}
