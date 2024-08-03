package org.mygen;

import org.mygen.codegenerate.database.DbReadTableUtil;
import org.mygen.codegenerate.generate.impl.CodeGenerateOne;
import org.mygen.codegenerate.generate.pojo.TableVo;
import org.mygen.codegenerate.generate.util.f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MyGen {

    public static void main(String[] args) {
        gen();
    }

    private static void gen() {
        HashSet<String> tableNames = new HashSet<>();
        // 表名
        tableNames.add("jw_apply");
        tableNames.add("jw_apply_user");
        // 包名
        String entityPackage = "jw";

        // Map<表名, 注释>, 自定义注释Map
        Map<String, String> commentMap = new HashMap<>();
        commentMap.put("i74mb_bahdjgbag", "云枢需求工单表");

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
                    // main使用online模板, 需要指定 default.one
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
