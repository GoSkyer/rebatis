package org.gosky.util;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

public final class ObjectUtils {

    /**
     * 检查对象属性是否为空
     *
     * @param object
     * @return
     */
    public static boolean checkObjAllFieldsIsNull(Object object) {
        if (null == object) {
            return true;
        }
        try {
            for (Field f : object.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                System.out.println(f.get(object).toString());
                //
                if (f.get(object) != null && !StringUtils.isEmpty(f.get(object).toString())) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
