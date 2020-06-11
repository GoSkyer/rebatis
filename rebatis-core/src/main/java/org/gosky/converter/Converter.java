package org.gosky.converter;

/**
 * @Auther: guozhong
 * @Date: 2019-04-02 16:44
 * @Description:
 */
public interface Converter {
//    Object convert(RowData rowData);

    String getName();

    Class getEntityClass();

//    abstract class Factory {
//
//        public Converter QueryResultConverter(QueryResult qr, Class pojo) {
//            return null;
//        }
//    }


}
