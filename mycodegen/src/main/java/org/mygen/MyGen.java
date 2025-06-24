package org.mygen;

import org.mygen.codegenerate.database.DbReadTableUtil;
import org.mygen.codegenerate.generate.impl.CodeGenerateOne;
import org.mygen.codegenerate.generate.pojo.TableVo;
import org.mygen.codegenerate.generate.util.f;

import java.util.*;

public class MyGen {

    public static void main(String[] args) {
        gen();
    }

    /**
     * 使用的是resources\mygen\code-template\one的模板
     */
    private static void gen() {
        Set<String> tableNames = new LinkedHashSet<>();
        // 表名
        tableNames.add("TEST_EMS_TRANSFORMER");
        // 包名
        String entityPackage = "design.test";

        // Map<表名, 注释>, 自定义注释Map
        Map<String, String> commentMap = new HashMap<>();
        commentMap.put("DOO_CRT_PACKAGE", "作业包");

        for (String tableName : tableNames) {
            try {
                Map<String, String> map = DbReadTableUtil.readTableDetail(tableName);
                if (map != null) {
                    TableVo tempTable = new TableVo();
                    tempTable.setTableName(tableName);
//                    tempTable.setPrimaryKeyPolicy("uuid");
                    tempTable.setEntityPackage(entityPackage);
                    tempTable.setEntityName(f.convertHumpFull(tableName));
                    tempTable.setFtlDescription(map.get("comment"));

                    String comment = commentMap.get(tableName);
                    if (comment != null) {
                        tempTable.setFtlDescription(comment);
                    }
                    // main使用online模板, 需要指定 default.one, online模板未修剪
//                    new CodeGenerateOne(tempTable).generateCodeFile("default.one");
                    new CodeGenerateOne(tempTable).generateCodeFile(null);
                }
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

        System.exit(0);
    }
}
