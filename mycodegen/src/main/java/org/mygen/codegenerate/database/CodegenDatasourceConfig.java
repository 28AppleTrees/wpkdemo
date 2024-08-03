package org.mygen.codegenerate.database;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import org.mygen.codegenerate.config.GenerateConfig;

public class CodegenDatasourceConfig {
    public CodegenDatasourceConfig() {
    }

    public static void initDbConfig(String DIVERNAME, String URL, String USERNAME, String PASSWORD) {
        GenerateConfig.diverName = DIVERNAME;
        GenerateConfig.url = URL;
        GenerateConfig.username = USERNAME;
        GenerateConfig.password = PASSWORD;
    }
}
