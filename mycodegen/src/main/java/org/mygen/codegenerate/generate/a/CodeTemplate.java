package org.mygen.codegenerate.generate.a;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CodeTemplate {
    private static final Logger log = LoggerFactory.getLogger(CodeTemplate.class);
    private String templatePath;
    private List<File> fileList = new ArrayList();
    private String stylePath;
    private String vueStyle;

    public CodeTemplate(String templatePath) {
        log.debug("----templatePath-----------------" + templatePath);
        log.debug("----stylePath-----------------" + this.stylePath);
        this.templatePath = templatePath;
    }

    private void setFile(File file) {
        this.setFileList(Collections.singletonList(file));
    }

    private void setFileList(File... var1) {
        this.fileList = Arrays.asList(var1);
    }

    public String getVueStyle() {
        return this.vueStyle;
    }

    public void setVueStyle(String vueStyle) {
        this.vueStyle = vueStyle;
    }

    public String getStylePath() {
        return this.stylePath;
    }

    public void setStylePath(String stylePath) {
        this.stylePath = stylePath;
    }

    public List<File> loadTemplate() {
        URL var1 = this.getClass().getResource(this.templatePath);
        if (var1 == null) {
            log.error(" >> 模板加载失败，请重新编译jeecg-system-biz项目，templatePath = " + this.templatePath);
        }

        String templateClassPath = this.getClass().getResource(this.templatePath).getFile();

        try {
            templateClassPath = URLDecoder.decode(templateClassPath, "utf-8");
        } catch (UnsupportedEncodingException var4) {
        }

        templateClassPath = templateClassPath.replaceAll("%20", " ");
        log.debug("-------classpath-------" + templateClassPath);
        if (templateClassPath.indexOf("/BOOT-INF/classes!") != -1 || templateClassPath.indexOf("/BOOT-INF/lib/") != -1 || templateClassPath.indexOf(".jar!") != -1) {
            templateClassPath = System.getProperty("user.dir") + File.separator + "config/jeecg/code-template-online/".replace("/", File.separator);
            log.debug("---JAR--config--classpath-------" + templateClassPath);
        }

        this.setFile(new File(templateClassPath));
        return this.fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }

    public String toString() {
        StringBuilder var1 = new StringBuilder();
        var1.append("{\"templateRootDirs\":\"");
        var1.append(this.fileList);
        var1.append("\",\"stylePath\":\"");
        var1.append(this.stylePath);
        var1.append("\",\"vueStyle\":\"");
        var1.append(this.vueStyle);
        var1.append("\"} ");
        return var1.toString();
    }
}
