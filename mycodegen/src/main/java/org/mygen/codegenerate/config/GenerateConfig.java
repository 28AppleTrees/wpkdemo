package org.mygen.codegenerate.config;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class GenerateConfig {
    private static final Logger log = LoggerFactory.getLogger(GenerateConfig.class);
    private static final String DEFAULT_DATABASE_FILE_PATH = "mygen/mygen_database";
    private static final String DEFAULT_CONFIG_FILE_PATH = "mygen/mygen_config";
    private static ResourceBundle databaseResourceBundle;
    private static ResourceBundle configResourceBundle;
    public static String a;
    public static String diverName;
    public static String url;
    public static String username;
    public static String password;
    public static String projectPath;
    public static String bussiPackage;
    public static String sourceRootPackage;
    public static String webrootPackage;
    public static String templatePath;
    public static boolean dbFiledConvertFlag;
    public static String primaryKeyField;
    public static String m;
    public static String pageSearchFiledNum;
    public static String pageFilterFields;
    public static String p;
    private static int v;
    private static boolean w;
    private static boolean x;
    private static boolean y;
    private static boolean z;
    private static boolean A;
    private static boolean B;

    // Controller 通用响应结果实体类名
    public static String resultName;
    // Controller 通用响应结果实体类路径
    public static String resultPackage;
    // Controller 通用响应结果实体类成功方法
    public static String resultMethodSuccess;
    // Controller 通用响应结果实体类失败方法
    public static String resultMethodError;

    public GenerateConfig() {
    }

    private static ResourceBundle readProperties(String filePath) {
        PropertyResourceBundle var1 = null;
        BufferedInputStream var2 = null;
        String var3 = System.getProperty("user.dir") + File.separator + "config" + File.separator + filePath + ".properties";

        try {
            var2 = new BufferedInputStream(new FileInputStream(var3));
            var1 = new PropertyResourceBundle(var2);
            var2.close();
            if (var1 != null) {
                log.info(" JAR方式部署，通过config目录读取配置：" + var3);
            }
        } catch (IOException var13) {
        } finally {
            if (var2 != null) {
                try {
                    var2.close();
                } catch (IOException var12) {
                    var12.printStackTrace();
                }
            }

        }

        return var1;
    }

    private void p() {
    }

    public static final String getDiverName() {
        return databaseResourceBundle.getString("diver_name");
    }

    public static final String getUrl() {
        return databaseResourceBundle.getString("url");
    }

    public static final String getUsername() {
        return databaseResourceBundle.getString("username");
    }

    public static final String getSchemaName() {
        return databaseResourceBundle.containsKey("schemaName") ? databaseResourceBundle.getString("schemaName") : null;
    }

    public static final String getPassword() {
        return databaseResourceBundle.getString("password");
    }

    public static final String getDatabaseName() {
        return databaseResourceBundle.getString("database_name");
    }

    public static final boolean getDbFiledConvert() {
        String config = configResourceBundle.getString("db_filed_convert");
        return !config.toString().equals("false");
    }

    private static String getBussiPackage() {
        return configResourceBundle.getString("bussi_package");
    }

    private static String getTemplatePath() {
        return configResourceBundle.getString("templatepath");
    }

    public static final String getSourceRootPackage() {
        return configResourceBundle.getString("source_root_package");
    }

    public static final String getWebrootPackage() {
        return configResourceBundle.getString("webroot_package");
    }

    public static final String getDbTableId() {
        return configResourceBundle.getString("db_table_id");
    }

    public static final String getPageFilterFields() {
        return configResourceBundle.getString("page_filter_fields");
    }

    public static final String getPageSearchFiledNum() {
        return configResourceBundle.getString("page_search_filed_num");
    }

    public static final String getResultName() {
        String value = configResourceBundle.getString("result_name");
        if (!checkPropertiesValue(value)) {
            return value;
        }
        return GenerateConfig.resultName;
    }

    public static final String getResultPackage() {
        String value = configResourceBundle.getString("result_package");
        if (!checkPropertiesValue(value)) {
            return value;
        }
        return GenerateConfig.resultPackage;
    }

    public static final String getResultMethodSuccess() {
        String value = configResourceBundle.getString("result_method_success");
        if (!checkPropertiesValue(value)) {
            return value;
        }
        return GenerateConfig.resultMethodSuccess;
    }

    public static final String getResultMethodError() {
        String value = configResourceBundle.getString("result_method_error");
        if (!checkPropertiesValue(value)) {
            return value;
        }
        return GenerateConfig.resultMethodError;
    }

    public static String getProjectPath() {
        String var0 = configResourceBundle.getString("project_path");
        if (var0 != null && !"".equals(var0)) {
            projectPath = var0;
        }

        return projectPath;
    }

    public static final String getPageFieldRequiredNum() {
        return configResourceBundle.getString("page_field_required_num");
    }

    public static void setProjectPath(String projectPath) {
        projectPath = projectPath;
    }

    public static void setTemplatePath(String var0) {
        templatePath = var0;
    }

    static {
        // todo mygen readProperties()路径错误, 无法读取配置文件
        databaseResourceBundle = readProperties("mygen/mygen_database");
        if (databaseResourceBundle == null) {
            log.debug("通过class目录加载配置文件 mygen/mygen_database");
            databaseResourceBundle = ResourceBundle.getBundle("mygen/mygen_database");
        }

        configResourceBundle = readProperties("mygen/mygen_config");
        if (configResourceBundle == null) {
            log.debug("通过class目录加载配置文件 mygen/mygen_config");
            configResourceBundle = ResourceBundle.getBundle("mygen/mygen_config");
        }

        a = "public";
        diverName = "com.mysql.jdbc.Driver";
        url = "jdbc:mysql://localhost:3306/jeecg-boot?useUnicode=true&characterEncoding=UTF-8";
        username = "root";
        password = "root";
        projectPath = "c:/workspace/jeecg";
        bussiPackage = "com.jeecg";
        sourceRootPackage = "src";
        webrootPackage = "WebRoot";
        templatePath = "/mygen/code-template/";
        dbFiledConvertFlag = true;
        m = "4";
        pageSearchFiledNum = "3";
        p = "1";
        diverName = getDiverName();
        url = getUrl();
        String var0 = getSchemaName();
        if (var0 != null && !"".equals(var0)) {
            a = var0;
        }

        resultName = "result";
        resultPackage = "org.jeecg.common.api.vo";
        resultMethodSuccess = "OK";
        resultMethodError = "error";

        username = getUsername();
        password = getPassword();
        sourceRootPackage = getSourceRootPackage();
        webrootPackage = getWebrootPackage();
        bussiPackage = getBussiPackage();
        templatePath = getTemplatePath();
        projectPath = getProjectPath();
        primaryKeyField = getDbTableId();
        dbFiledConvertFlag = getDbFiledConvert();
        pageFilterFields = getPageFilterFields();
        pageSearchFiledNum = getPageSearchFiledNum();
        sourceRootPackage = sourceRootPackage.replace(".", "/");
        webrootPackage = webrootPackage.replace(".", "/");
        v = 0;
        w = false;
        x = false;
        y = false;
        z = false;
        A = false;
        B = false;

        resultName = getResultName();
        resultPackage = getResultPackage();
        resultMethodSuccess = getResultMethodSuccess();
        resultMethodError = getResultMethodError();

        try {
            w = null != Class.forName("org.apache.jsp.designer.index_jsp");
            if (w) {
                ++v;
            }
        } catch (Throwable var9) {
            w = false;
        }

        try {
            x = null != Class.forName("org.apache.jsp.designer.candidateUsersConfig_jsp");
            if (x) {
                ++v;
            }
        } catch (Throwable var8) {
            x = false;
        }

        try {
            y = null != Class.forName("org.jeecg.modules.online.desform.entity.DesignForm");
            if (y) {
                ++v;
            }
        } catch (Throwable var7) {
            y = false;
        }

        try {
            z = null != Class.forName("org.jeecg.modules.online.desform.service.IDesignFormAuthService");
            if (z) {
                ++v;
            }
        } catch (Throwable var6) {
            z = false;
        }

        try {
            A = null != Class.forName("org.jeecg.modules.aspect.SysUserAspect");
            if (A) {
                ++v;
            }
        } catch (Throwable var5) {
            A = false;
        }

        try {
            B = null != Class.forName("org.jeecg.modules.extbpm.listener.execution.ProcessEndListener");
            if (B) {
                ++v;
            }
        } catch (Throwable var4) {
            B = false;
        }

        long var10 = 1728000000L;
        Runnable var2 = new Runnable() {
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1728000000L);
                        if (GenerateConfig.v <= 2) {
                            Thread.sleep(787968000000L);
                            return;
                        }

                        String var1 = "";
                        Object var2 = null;

                        try {
                            String var4 = System.getProperty("user.dir") + File.separator + "config" + File.separator + org.mygen.codegenerate.generate.util.d.e();
                            BufferedInputStream var3 = new BufferedInputStream(new FileInputStream(var4));
                            var2 = new PropertyResourceBundle(var3);
                            var3.close();
                        } catch (IOException var6) {
                        }

                        if (var2 == null) {
                            var2 = ResourceBundle.getBundle(org.mygen.codegenerate.generate.util.d.d());
                        }

                        String var8 = ((ResourceBundle)var2).getString(org.mygen.codegenerate.generate.util.d.g());
                        byte[] var9 = org.mygen.codegenerate.generate.util.d.a(org.mygen.codegenerate.generate.util.d.i(), var8);
                        var8 = new String(var9, "UTF-8");
                        String[] var5 = var8.split("\\|");
                        if (var8.contains("--")) {
                            Thread.sleep(787968000000L);
                            return;
                        }

                        if (!var5[1].equals(org.mygen.codegenerate.generate.util.e.b())) {
                            System.out.println(org.mygen.codegenerate.generate.util.d.h() + org.mygen.codegenerate.generate.util.e.b());
                            System.err.println(org.mygen.codegenerate.generate.util.c.d("9RUvZRL/eoRJhWiHinvL3IFhdT4m8hwt7o9OXN5JPAPcpelJxtgYL0/JESq9cif96ihcHzCZ5d7V6meXp1InTNjyffi6mPzwXLlrdruW38M=", "jm072"));
                            System.exit(0);
                        }
                    } catch (Exception var7) {
                        System.err.println(org.mygen.codegenerate.generate.util.d.h() + org.mygen.codegenerate.generate.util.e.b());
                        System.err.println(org.mygen.codegenerate.generate.util.c.d("RXPUfpgyxAmQAY+315PkFvzSFm7dkFSwselDafKC8PVxQOWwkRbJSXVlhZ3NyxTGfJJO9ES9iOmfXtI+mgMNTg==", "jm0156"));
                        System.exit(0);
                    }
                }
            }
        };
        Thread var3 = new Thread(var2);
        var3.start();
    }

    private static boolean checkPropertiesValue(String value) {
        return value == null || value.equals("");
    }
}
