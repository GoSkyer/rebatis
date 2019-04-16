package org.gosky.rebatis.sample;

import com.github.jasync.sql.db.RowData;

import org.gosky.converter.Converter;

/**
 * @Auther: guozhong
 * @Date: 2019-04-02 17:07
 * @Description:
 */
public class TestConverter implements Converter {

    @Override
    public Object convert(RowData rowData) {
        return null;
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
