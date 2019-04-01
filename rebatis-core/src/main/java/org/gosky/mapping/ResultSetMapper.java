package org.gosky.mapping;

import com.github.jasync.sql.db.QueryResult;

/**
 * @Auther: guozhong
 * @Date: 2019-04-01 14:53
 * @Description:
 */
public interface ResultSetMapper {

    <T> T parser(QueryResult qr, T pojo);

}
