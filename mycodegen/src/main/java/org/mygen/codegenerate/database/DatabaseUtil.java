package org.mygen.codegenerate.database;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseUtil {
    private static final Logger a = LoggerFactory.getLogger(DatabaseUtil.class);

    public DatabaseUtil() {
    }

    /**
     * 判断是什么类型的数据库, 执行不同的sql
     * @param url
     * @return
     */
    public static boolean a(String url) {
        return containsSqlType(url, "mysql") || containsSqlType(url, "mariadb") || containsSqlType(url, "sqlite") || containsSqlType(url, "clickhouse") || containsSqlType(url, "polardb");
    }

    public static boolean b(String url) {
        return containsSqlType(url, "oracle9i") || containsSqlType(url, "oracle") || containsSqlType(url, "dm") || containsSqlType(url, "edb");
    }

    public static boolean c(String url) {
        return containsSqlType(url, "sqlserver") || containsSqlType(url, "sqlserver2012") || containsSqlType(url, "derby");
    }

    public static boolean d(String url) {
        return containsSqlType(url, "postgresql") || containsSqlType(url, "kingbase") || containsSqlType(url, "zenith");
    }

    private static boolean containsSqlType(String url, String sqlType) {
        String var2 = "jdbc:" + sqlType;
        return url.toLowerCase().contains(var2);
    }
}
