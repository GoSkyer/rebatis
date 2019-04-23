package org.gosky.rebatis.sample;

import com.github.jasync.sql.db.QueryResult;

import java.lang.reflect.Field;

public class ResultSetMapper<T> {

    public static <T> T parseResultSet(QueryResult qr, Class<T> cls) {
        try {
            //只会将查询的第一天数据转换为对象
            if (qr.getRowsAffected() == 1) {
                //实例化对象
                T obj = cls.newInstance();
                //获取类中所有的属性
                Field[] arrf = cls.getDeclaredFields();
                //遍历属性
                for (Field f : arrf) {
                    //设置忽略访问校验
                    f.setAccessible(true);
                    //为属性设置内容
                    f.set(obj, qr.getRows().get(0).get(f.getName()));
                }
                return obj;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
