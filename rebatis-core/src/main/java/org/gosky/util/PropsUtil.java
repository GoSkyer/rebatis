package org.gosky.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * @author zhiqin.zhang
 * 属性文件工具类
 */
@Slf4j
public final class PropsUtil {

    public static Properties loadProps(String filePath) {
        Properties properties = new Properties();
        try {
            properties.load(PropsUtil.class.getResourceAsStream(filePath));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(" fail load properties case:{}", e.getMessage());
        }
        return properties;
    }

    /**
     * 读取配置文件
     *
     * @param key
     * @return
     */
    public static String getString(Properties properties, String key) {
        return properties.getProperty(key);
    }

}
