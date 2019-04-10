package org.gosky.converter;

import com.github.jasync.sql.db.QueryResult;

/**
 * @Auther: guozhong
 * @Date: 2019-04-02 16:44
 * @Description:
 */
public interface Converter {
    Object convert(QueryResult qr);

    String getName();

    Class getEntityClass();

//    abstract class Factory {
//
//        public Converter QueryResultConverter(QueryResult qr, Class pojo) {
//            return null;
//        }
//    }


}
