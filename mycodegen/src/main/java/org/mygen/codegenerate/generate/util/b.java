package org.mygen.codegenerate.generate.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class b {
    private static final Logger log = LoggerFactory.getLogger(b.class);

    public b() {
    }

    public static Configuration buildConfiguration(List<File> var0, String var1, String var2) throws IOException {
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        log.debug(" FileTemplateLoader[] size " + var0.size());
        log.debug(" templateRootDirs templateName " + var2);
        FileTemplateLoader[] var4 = new FileTemplateLoader[var0.size()];

        for(int i = 0; i < var0.size(); ++i) {
            File file = (File)var0.get(i);
            log.debug(" FileTemplateLoader " + file.getAbsolutePath());
            var4[i] = new FileTemplateLoader(file);
        }

        MultiTemplateLoader var7 = new MultiTemplateLoader(var4);
        configuration.setTemplateLoader(var7);
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
        configuration.setNumberFormat("###############");
        configuration.setBooleanFormat("true,false");
        configuration.setDefaultEncoding(var1);
        return configuration;
    }

    public static List<String> a(String var0, String var1) {
        String[] var2 = b(var0, "\\/");
        ArrayList var3 = new ArrayList();
        var3.add(var1);
        var3.add(File.separator + var1);
        String var4 = "";

        for(int var5 = 0; var5 < var2.length; ++var5) {
            var4 = var4 + File.separator + var2[var5];
            var3.add(var4 + File.separator + var1);
        }

        return var3;
    }

    public static String[] b(String var0, String var1) {
        if (var0 == null) {
            return new String[0];
        } else {
            StringTokenizer var2 = new StringTokenizer(var0, var1);
            ArrayList var3 = new ArrayList();

            while(var2.hasMoreElements()) {
                Object var4 = var2.nextElement();
                var3.add(var4.toString());
            }

            return (String[])var3.toArray(new String[var3.size()]);
        }
    }

    /**
     * 解析模板路径并填充
     * @param sourceTemplatePath
     * @param var1
     * @param var2
     * @return
     */
    public static String parseFilePath(String sourceTemplatePath, Map<String, Object> var1, Configuration var2) {
        StringWriter var3 = new StringWriter();

        try {
            Template var4 = new Template("templateString...", new StringReader(sourceTemplatePath), var2);
            var4.process(var1, var3);
            return var3.toString();
        } catch (Exception var5) {
            throw new IllegalStateException("cannot process templateString:" + sourceTemplatePath + " cause:" + var5, var5);
        }
    }

    /**
     * 创建模板文件并填充模板内容
     * @param template
     * @param tableData
     * @param outputFile
     * @param fileEncoding
     * @throws IOException
     * @throws TemplateException
     */
    public static void createFileAndFill(Template template, Map<String, Object> tableData, File outputFile, String fileEncoding) throws IOException, TemplateException {
        // new FileOutputStream() 会自动创建不存在的文件, writer进行模板内容续写
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), fileEncoding));
        tableData.put("Format", new SimpleFormat());
        template.process(tableData, writer);
        writer.close();
    }
}
