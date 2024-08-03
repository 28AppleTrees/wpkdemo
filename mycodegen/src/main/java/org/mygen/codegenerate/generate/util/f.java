package org.mygen.codegenerate.generate.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import org.apache.commons.lang3.StringUtils;
import org.mygen.codegenerate.generate.pojo.ColumnVo;

public class f {
    public f() {
    }

    public static String a(String var0) {
        if (!"YES".equals(var0) && !"yes".equals(var0) && !"y".equals(var0) && !"Y".equals(var0) && !"f".equals(var0)) {
            return !"NO".equals(var0) && !"N".equals(var0) && !"no".equals(var0) && !"n".equals(var0) && !"t".equals(var0) ? null : "N";
        } else {
            return "Y";
        }
    }

    public static String b(String var0) {
        return StringUtils.isBlank(var0) ? "" : var0;
    }

    public static String c(String var0) {
        return "'" + var0 + "'";
    }

    /**
     * 转驼峰, 首个单词首字母不变
     * @param field
     * @return
     */
    public static String convertHump(String field) {
        String[] var1 = field.split("_");
        field = "";
        int var2 = 0;

        for(int var3 = var1.length; var2 < var3; ++var2) {
            if (var2 > 0) {
                String var4 = var1[var2].toLowerCase();
                var4 = var4.substring(0, 1).toUpperCase() + var4.substring(1, var4.length());
                field = field + var4;
            } else {
                field = field + var1[var2].toLowerCase();
            }
        }

        return field;
    }

    /**
     * 转驼峰, 首个单词首字母大写
     * @param sourceString
     * @return
     */
    public static String convertHumpFull(String sourceString) {
        String[] split = sourceString.split("_");
        sourceString = "";
        for (int i = 0; i < split.length; i++) {
            String var4 = split[i].toLowerCase();
            var4 = var4.substring(0, 1).toUpperCase() + var4.substring(1, var4.length());
            sourceString = sourceString + var4;
        }
        return sourceString;
    }

    public static void a(ColumnVo var0) {
        String var1 = var0.getFieldType();
        String var2 = var0.getScale();
        var0.setClassType("inputxt");
        if ("N".equals(var0.getNullable())) {
            var0.setOptionType("*");
        }

        if (!"datetime".equals(var1) && !var1.contains("time")) {
            if ("date".equals(var1)) {
                var0.setClassType("easyui-datebox");
            } else if (var1.contains("int")) {
                var0.setOptionType("n");
            } else if ("number".equals(var1)) {
                if (StringUtils.isNotBlank(var2) && Integer.parseInt(var2) > 0) {
                    var0.setOptionType("d");
                }
            } else if (!"float".equals(var1) && !"double".equals(var1) && !"decimal".equals(var1)) {
                if ("numeric".equals(var1)) {
                    var0.setOptionType("d");
                }
            } else {
                var0.setOptionType("d");
            }
        } else {
            var0.setClassType("easyui-datetimebox");
        }

    }

    public static String a(String var0, String var1, String var2) {
        if (var0.contains("char")) {
            var0 = "java.lang.String";
        } else if (var0.contains("int")) {
            var0 = "java.lang.Integer";
        } else if (var0.contains("float")) {
            var0 = "java.lang.Float";
        } else if (var0.contains("double")) {
            var0 = "java.lang.Double";
        } else if (var0.contains("number")) {
            if (StringUtils.isNotBlank(var2) && Integer.parseInt(var2) > 0) {
                var0 = "java.math.BigDecimal";
            } else if (StringUtils.isNotBlank(var1) && Integer.parseInt(var1) > 10) {
                var0 = "java.lang.Long";
            } else {
                var0 = "java.lang.Integer";
            }
        } else if (var0.contains("decimal")) {
            var0 = "java.math.BigDecimal";
        } else if (var0.contains("date")) {
            var0 = "java.util.Date";
        } else if (var0.contains("time")) {
            var0 = "java.util.Date";
        } else if (var0.contains("blob")) {
            var0 = "byte[]";
        } else if (var0.contains("clob")) {
            var0 = "java.sql.Clob";
        } else if (var0.contains("numeric")) {
            var0 = "java.math.BigDecimal";
        } else {
            var0 = "java.lang.Object";
        }

        return var0;
    }
}