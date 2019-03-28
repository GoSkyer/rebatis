package org.gosky.util;

import org.gosky.config.ConfigConstant;

import java.util.Properties;

/**
 * @author zhiqin.zhang
 */
public final class ConfigHelper {

    private static final Properties CONFIG_PROS = PropsUtil.loadProps("/" + ConfigConstant.CONFIG_FILE);

    public static String getJdbcDriver() {
        return PropsUtil.getString(CONFIG_PROS, ConfigConstant.JDBC_DRIVER);
    }

    public static String getJdbcUrl() {
        return PropsUtil.getString(CONFIG_PROS, ConfigConstant.JDBC_URL);
    }


    public static String getJdbcUsername() {
        return PropsUtil.getString(CONFIG_PROS, ConfigConstant.JDBC_USERNAME);
    }


    public static String getJdbcPassword() {
        return PropsUtil.getString(CONFIG_PROS, ConfigConstant.JDBC_PASSWORD);
    }

    public static String getAppBasePackage() {
        return PropsUtil.getString(CONFIG_PROS, ConfigConstant.APP_BASE_PACKEAGE);
    }

    public static String getAppJspPath() {
        return PropsUtil.getString(CONFIG_PROS, ConfigConstant.APP_JSP_PATH);
    }

    public static String getAppAssetPath() {
        return PropsUtil.getString(CONFIG_PROS, ConfigConstant.APP_ASSET_PATH);
    }

}
