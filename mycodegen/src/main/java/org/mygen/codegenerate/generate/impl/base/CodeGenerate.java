package org.mygen.codegenerate.generate.impl.base;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.mygen.codegenerate.config.GenerateConfig;
import org.mygen.codegenerate.generate.a.CodeTemplate;
import org.mygen.codegenerate.generate.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CodeGenerate {
    private static final Logger log = LoggerFactory.getLogger(CodeGenerate.class);
    protected static String fileEncoding = "UTF-8";
    private static final String b = "__";
    protected List<String> infoList = new ArrayList();

    public CodeGenerate() {
    }

    protected void generate(CodeTemplate codeTemplate, String projectPath, Map<String, Object> tableData) throws Exception {
        log.debug("--------generate----projectPath--------" + projectPath);

        for(int i = 0; i < codeTemplate.loadTemplate().size(); ++i) {
            File var5 = (File)codeTemplate.loadTemplate().get(i);
            this.generate(projectPath, var5, tableData, codeTemplate);
        }

    }

    protected void generate(String projectPath, File templateRootFile, Map<String, Object> tableData, CodeTemplate codeTemplate) throws Exception {
        if (templateRootFile == null) {
            throw new IllegalStateException("'templateRootDir' must be not null");
        } else {
            log.info("  load template from templateRootDir = '" + templateRootFile.getAbsolutePath() + "',stylePath ='" + codeTemplate.getStylePath() + "',  out GenerateRootDir:" + GenerateConfig.projectPath);
            List<File> var5 = FileUtil.loadAllFile(templateRootFile);
            log.debug("----srcFiles----size-----------" + var5.size());
            log.debug("----srcFiles----list------------" + var5.toString());

            for(int i = 0; i < var5.size(); ++i) {
                File var7 = (File)var5.get(i);
                this.generate(projectPath, templateRootFile, tableData, var7, codeTemplate);
            }

        }
    }

    protected void generate(String projectPath, File var2, Map<String, Object> tableData, File var4, CodeTemplate codeTemplate) throws Exception {
        log.debug("-------templateRootDir--" + var2.getPath());
        log.debug("-------srcFile--" + var4.getPath());
        String templateFilePath = FileUtil.a(var2, var4);

        try {
            log.debug("-------templateFile--" + templateFilePath);
            if (codeTemplate.getStylePath() != null && !"".equals(codeTemplate.getStylePath()) && !templateFilePath.replace(File.separator, ".").startsWith(codeTemplate.getStylePath())) {
                return;
            }

            String outputFilepath = buildOutputFilepath(tableData, templateFilePath, codeTemplate);
            log.debug("-------outputFilepath--" + outputFilepath);
            String var8;
            if (outputFilepath.startsWith("java")) {
                var8 = projectPath + File.separator + GenerateConfig.sourceRootPackage.replace(".", File.separator);
                outputFilepath = outputFilepath.substring("java".length());
                outputFilepath = var8 + outputFilepath;
                log.debug("-------java----outputFilepath--" + outputFilepath);
                this.generate(templateFilePath, outputFilepath, tableData, codeTemplate);
            } else if (outputFilepath.startsWith("webapp")) {
                var8 = projectPath + File.separator + GenerateConfig.webrootPackage.replace(".", File.separator);
                outputFilepath = outputFilepath.substring("webapp".length());
                outputFilepath = var8 + outputFilepath;
                log.debug("-------webapp---outputFilepath---" + outputFilepath);
                this.generate(templateFilePath, outputFilepath, tableData, codeTemplate);
            }
        } catch (Exception var10) {
            log.error(var10.toString(), var10);
        }

    }

    protected void generate(String templateFilePath, String outputFilepath, Map<String, Object> tableData, CodeTemplate codeTemplate) throws Exception {
        if (outputFilepath.endsWith("i")) {
            outputFilepath = outputFilepath.substring(0, outputFilepath.length() - 1);
        }

        if (outputFilepath.contains("__")) {
            outputFilepath = outputFilepath.replace("__", ".");
        }

        if (!outputFilepath.contains("vue") || codeTemplate == null || !org.mygen.codegenerate.generate.util.g.c((Object) codeTemplate.getVueStyle()) || outputFilepath.contains(codeTemplate.getVueStyle() + File.separator)) {
            Template template = this.a(templateFilePath, codeTemplate);
            template.setOutputEncoding(fileEncoding);
            File outputFile = FileUtil.createParentDirs(outputFilepath);
            log.info("[generate]\t template:" + templateFilePath + " ==> " + outputFilepath);
            org.mygen.codegenerate.generate.util.b.createFileAndFill(template, tableData, outputFile, fileEncoding);
            if (!this.a(outputFile)) {
                this.infoList.add("生成成功：" + outputFilepath);
            }

            if (this.a(outputFile)) {
                this.a(outputFile, "#segment#");
            }

        }
    }

    protected Template a(String var1, CodeTemplate var2) throws IOException {
        return org.mygen.codegenerate.generate.util.b.buildConfiguration(var2.loadTemplate(), fileEncoding, var1).getTemplate(var1);
    }

    protected boolean a(File var1) {
        return var1.getName().startsWith("[1-n]");
    }

    protected void a(File var1, String var2) {
        InputStreamReader var3 = null;
        BufferedReader var4 = null;
        ArrayList var5 = new ArrayList();
        boolean var20 = false;

        int var28;
        label341: {
            label342: {
                try {
                    var20 = true;
                    var3 = new InputStreamReader(new FileInputStream(var1), "UTF-8");
                    var4 = new BufferedReader(var3);
                    boolean var7 = false;
                    OutputStreamWriter var8 = null;

                    while(true) {
                        String var6;
                        while((var6 = var4.readLine()) != null) {
                            if (var6.trim().length() > 0 && var6.startsWith(var2)) {
                                String var9 = var6.substring(var2.length());
                                String var10 = var1.getParentFile().getAbsolutePath();
                                var9 = var10 + File.separator + var9;
                                log.info("[generate]\t split file:" + var1.getAbsolutePath() + " ==> " + var9);
                                var8 = new OutputStreamWriter(new FileOutputStream(var9), "UTF-8");
                                var5.add(var8);
                                this.infoList.add("生成成功：" + var9);
                                var7 = true;
                            } else if (var7) {
                                var8.append(var6 + "\r\n");
                            }
                        }

                        for(int var29 = 0; var29 < var5.size(); ++var29) {
                            ((Writer)var5.get(var29)).close();
                        }

                        var4.close();
                        var3.close();
                        log.debug("[generate]\t delete file:" + var1.getAbsolutePath());
                        b(var1);
                        var20 = false;
                        break label341;
                    }
                } catch (FileNotFoundException var25) {
                    var25.printStackTrace();
                    var20 = false;
                    break label342;
                } catch (IOException var26) {
                    var26.printStackTrace();
                    var20 = false;
                } finally {
                    if (var20) {
                        try {
                            if (var4 != null) {
                                var4.close();
                            }

                            if (var3 != null) {
                                var3.close();
                            }

                            if (var5.size() > 0) {
                                for(int var12 = 0; var12 < var5.size(); ++var12) {
                                    if (var5.get(var12) != null) {
                                        ((Writer)var5.get(var12)).close();
                                    }
                                }
                            }
                        } catch (IOException var21) {
                            var21.printStackTrace();
                        }

                    }
                }

                try {
                    if (var4 != null) {
                        var4.close();
                    }

                    if (var3 != null) {
                        var3.close();
                    }

                    if (var5.size() > 0) {
                        for(var28 = 0; var28 < var5.size(); ++var28) {
                            if (var5.get(var28) != null) {
                                ((Writer)var5.get(var28)).close();
                            }
                        }
                    }
                } catch (IOException var22) {
                    var22.printStackTrace();
                }

                return;
            }

            try {
                if (var4 != null) {
                    var4.close();
                }

                if (var3 != null) {
                    var3.close();
                }

                if (var5.size() > 0) {
                    for(var28 = 0; var28 < var5.size(); ++var28) {
                        if (var5.get(var28) != null) {
                            ((Writer)var5.get(var28)).close();
                        }
                    }
                }
            } catch (IOException var23) {
                var23.printStackTrace();
            }

            return;
        }

        try {
            if (var4 != null) {
                var4.close();
            }

            if (var3 != null) {
                var3.close();
            }

            if (var5.size() > 0) {
                for(var28 = 0; var28 < var5.size(); ++var28) {
                    if (var5.get(var28) != null) {
                        ((Writer)var5.get(var28)).close();
                    }
                }
            }
        } catch (IOException var24) {
            var24.printStackTrace();
        }

    }

    protected static String buildOutputFilepath(Map<String, Object> var0, String templatePath, CodeTemplate codeTemplate) throws Exception {
        String var3 = templatePath;
        boolean var4 = true;
        int var9;
        if ((var9 = templatePath.indexOf(64)) != -1) {
            var3 = templatePath.substring(0, var9);
            String var5 = templatePath.substring(var9 + 1);
            Object var6 = var0.get(var5);
            if (var6 == null) {
                System.err.println("[not-generate] WARN: test expression is null by key:[" + var5 + "] on template:[" + templatePath + "]");
                return null;
            }

            if (!"true".equals(String.valueOf(var6))) {
                log.error("[not-generate]\t test expression '@" + var5 + "' is false,template:" + templatePath);
                return null;
            }
        }

        Configuration var10 = org.mygen.codegenerate.generate.util.b.buildConfiguration(codeTemplate.loadTemplate(), fileEncoding, "/");
        var3 = org.mygen.codegenerate.generate.util.b.parseFilePath(var3, var0, var10);
        String var11 = codeTemplate.getStylePath();
        if (var11 != null && var11 != "") {
            var3 = var3.substring(var11.length() + 1);
        }

        String var7 = var3.substring(var3.lastIndexOf("."));
        String var8 = var3.substring(0, var3.lastIndexOf(".")).replace(".", File.separator);
        var3 = var8 + var7;
        return var3;
    }

    protected static boolean b(File var0) {
        boolean var1 = false;

        for(int var2 = 0; !var1 && var2++ < 10; var1 = var0.delete()) {
            System.gc();
        }

        return var1;
    }

    /**
     * 去除字符串前缀后缀
     * @param sourceString
     * @param charStr
     * @return
     */
    protected static String trimChar(String sourceString, String charStr) {
        boolean var2 = true;
        boolean var3 = true;

        do {
            int startIndex = sourceString.indexOf(charStr) == 0 ? 1 : 0;
            int endIndex = sourceString.lastIndexOf(charStr) + 1 == sourceString.length() ? sourceString.lastIndexOf(charStr) : sourceString.length();
            sourceString = sourceString.substring(startIndex, endIndex);
            var2 = sourceString.indexOf(charStr) == 0;
            var3 = sourceString.lastIndexOf(charStr) + 1 == sourceString.length();
        } while(var2 || var3);

        return sourceString;
    }
}
