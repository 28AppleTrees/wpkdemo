package org.mygen.codegenerate.generate.impl;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import org.apache.commons.lang3.StringUtils;
import org.mygen.codegenerate.config.GenerateConfig;
import org.mygen.codegenerate.database.DbReadTableUtil;
import org.mygen.codegenerate.generate.IGenerate;
import org.mygen.codegenerate.generate.a.CodeTemplate;
import org.mygen.codegenerate.generate.impl.base.CodeGenerate;
import org.mygen.codegenerate.generate.pojo.ColumnVo;
import org.mygen.codegenerate.generate.pojo.TableVo;
import org.mygen.codegenerate.generate.util.NonceUtils;
import org.mygen.codegenerate.generate.util.f;
import org.mygen.codegenerate.generate.util.g;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CodeGenerateOne extends CodeGenerate implements IGenerate {
    private static final Logger log = LoggerFactory.getLogger(CodeGenerateOne.class);
    private TableVo tableVo;
    private List<ColumnVo> columnList;
    private List<ColumnVo> originalTableColumnList;

    public CodeGenerateOne(TableVo tableVo) {
        this.tableVo = tableVo;
    }

    public CodeGenerateOne(TableVo tableVo, List<ColumnVo> columns, List<ColumnVo> originalColumns) {
        this.tableVo = tableVo;
        this.columnList = columns;
        this.originalTableColumnList = originalColumns;
    }

    public Map<String, Object> loadConfigAndReadTable() throws Exception {
        HashMap<String, Object> resultMap = new HashMap<>();

        resultMap.put("resultPackage", GenerateConfig.resultPackage);
        resultMap.put("resultName", GenerateConfig.resultName);
        resultMap.put("resultMethodSuccess", GenerateConfig.resultMethodSuccess);
        resultMap.put("resultMethodError", GenerateConfig.resultMethodError);

        resultMap.put("bussiPackage", GenerateConfig.bussiPackage);
        resultMap.put("entityPackage", this.tableVo.getEntityPackage());
        resultMap.put("entityName", this.tableVo.getEntityName());
        resultMap.put("tableName", this.tableVo.getTableName());
        resultMap.put("primaryKeyField", GenerateConfig.primaryKeyField);
        if (this.tableVo.getFieldRequiredNum() == null) {
            this.tableVo.setFieldRequiredNum(StringUtils.isNotEmpty(GenerateConfig.m) ? Integer.parseInt(GenerateConfig.m) : -1);
        }

        if (this.tableVo.getSearchFieldNum() == null) {
            this.tableVo.setSearchFieldNum(StringUtils.isNotEmpty(GenerateConfig.pageSearchFiledNum) ? Integer.parseInt(GenerateConfig.pageSearchFiledNum) : -1);
        }

        if (this.tableVo.getFieldRowNum() == null) {
            this.tableVo.setFieldRowNum(Integer.parseInt(GenerateConfig.p));
        }

        resultMap.put("tableVo", this.tableVo);

        try {
            if (this.columnList == null || this.columnList.size() == 0) {
                this.columnList = DbReadTableUtil.readTableColumn(this.tableVo.getTableName());
            }

            resultMap.put("columns", this.columnList);
            if (this.originalTableColumnList == null || this.originalTableColumnList.size() == 0) {
                this.originalTableColumnList = DbReadTableUtil.readOriginalTableColumn(this.tableVo.getTableName());
            }

            resultMap.put("originalColumns", this.originalTableColumnList);
            Iterator var2 = this.originalTableColumnList.iterator();

            while(var2.hasNext()) {
                ColumnVo var3 = (ColumnVo)var2.next();
                if (var3.getFieldName().toLowerCase().equals(GenerateConfig.primaryKeyField.toLowerCase())) {
                    resultMap.put("primaryKeyPolicy", var3.getFieldType());
                }
            }
        } catch (Exception var4) {
            throw var4;
        }

        long var5 = NonceUtils.c() + NonceUtils.g();
        resultMap.put("serialVersionUID", String.valueOf(var5));
        log.info("load template data: " + resultMap.toString());
        return resultMap;
    }

    public List<String> generateCodeFile(String stylePath) throws Exception {
        log.debug("----jeecg---Code----Generation----[单表模型:" + this.tableVo.getTableName() + "]------- 生成中。。。");
        String projectPath = GenerateConfig.projectPath;
        Map<String, Object> tableData = this.loadConfigAndReadTable();
        String templatePath = GenerateConfig.templatePath;
        // 模板路径默认为`jeecg/code-template`时, 追加`/one`
        if (trimChar(templatePath, "/").equals("mygen/code-template")) {
            templatePath = "/" + trimChar(templatePath, "/") + "/one";
            GenerateConfig.setTemplatePath(templatePath);
        }
        CodeTemplate codeTemplate = new CodeTemplate(templatePath);
        codeTemplate.setStylePath(stylePath);
        if (this.tableVo != null && this.tableVo.getExtendParams() != null) {
            codeTemplate.setVueStyle(g.a(this.tableVo.getExtendParams().get("vueStyle"), "vue"));
        }

        this.generate(codeTemplate, projectPath, tableData);
        log.info(" ----- jeecg-boot ---- generate  code  success =======> 表名：" + this.tableVo.getTableName() + " ");
        return this.infoList;
    }

    public List<String> generateCodeFile(String projectPath, String templatePath, String stylePath) throws Exception {
        if (projectPath != null && !"".equals(projectPath)) {
            GenerateConfig.setProjectPath(projectPath);
        }

        if (templatePath != null && !"".equals(templatePath)) {
            GenerateConfig.setTemplatePath(templatePath);
        }

        this.generateCodeFile(stylePath);
        return this.infoList;
    }

    public static void main(String[] args) {
        HashSet<String> tableNames = new HashSet<>();
        tableNames.add("jw_network_disk");
//        tableNames.add("expos_social_project_detail_copy1");
//        tableNames.add("abc");
//        tableNames.add("abds");
        String entityPackage = "jw";

        // Map<表名, 注释>, 自定义注释Map
        Map<String, String> commentMap = new HashMap<>();
        commentMap.put("demo", "demo注释修改");

        try {
            for (String tableName : tableNames) {
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

//                    new CodeGenerateOne(tempTable).generateCodeFile("default.one");
                    new CodeGenerateOne(tempTable).generateCodeFile(null);
                }
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        System.exit(0);
    }

}
