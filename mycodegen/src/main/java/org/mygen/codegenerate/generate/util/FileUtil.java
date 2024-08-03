package org.mygen.codegenerate.generate.util;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileUtil {
    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);
    public static List<String> skipFileList = new ArrayList();
    public static List<String> skipFileList2 = new ArrayList();

    public FileUtil() {
    }

    /**
     *
     * @param rootFile
     * @return
     * @throws IOException
     */
    public static List<File> loadAllFile(File rootFile) throws IOException {
        ArrayList<File> fileList = new ArrayList<>();
        loadAllFile(rootFile, (List)fileList);
        Collections.sort(fileList, new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
            }
        });
        return fileList;
    }

    public static void loadAllFile(File rootFile, List<File> fileList) throws IOException {
        log.debug("---------dir------------path: " + rootFile.getPath() + " -- isHidden --: " + rootFile.isHidden() + " -- isDirectory --: " + rootFile.isDirectory());
        if (!rootFile.isHidden() && rootFile.isDirectory() && !isSkip(rootFile)) {
            File[] rootFileArr = rootFile.listFiles();

            for(int i = 0; i < rootFileArr.length; ++i) {
                loadAllFile(rootFileArr[i], fileList);
            }
        } else if (!isSkip2(rootFile) && !isSkip(rootFile)) {
            fileList.add(rootFile);
        }

    }

    public static String a(File var0, File var1) {
        if (var0.equals(var1)) {
            return "";
        } else {
            return var0.getParentFile() == null ? var1.getAbsolutePath().substring(var0.getAbsolutePath().length()) : var1.getAbsolutePath().substring(var0.getAbsolutePath().length() + 1);
        }
    }

    public static boolean b(File var0) {
        return var0.isDirectory() ? false : a(var0.getName());
    }

    public static boolean a(String var0) {
        return !StringUtils.isBlank(b(var0));
    }

    public static String b(String var0) {
        if (var0 == null) {
            return null;
        } else {
            int var1 = var0.indexOf(".");
            return var1 == -1 ? "" : var0.substring(var1 + 1);
        }
    }

    public static File createParentDirs(String filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("file must be not null");
        } else {
            File file = new File(filePath);
            mkParentDirs(file);
            return file;
        }
    }

    public static void mkParentDirs(File file) {
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

    }

    private static boolean isSkip(File file) {
        for(int i = 0; i < skipFileList.size(); ++i) {
            if (file.getName().equals(skipFileList.get(i))) {
                return true;
            }
        }

        return false;
    }

    private static boolean isSkip2(File file) {
        for(int i = 0; i < skipFileList2.size(); ++i) {
            if (file.getName().endsWith((String) skipFileList2.get(i))) {
                return true;
            }
        }

        return false;
    }

    static {
        skipFileList.add(".svn");
        skipFileList.add("CVS");
        skipFileList.add(".cvsignore");
        skipFileList.add(".copyarea.db");
        skipFileList.add("SCCS");
        skipFileList.add("vssver.scc");
        skipFileList.add(".DS_Store");
        skipFileList.add(".git");
        skipFileList.add(".gitignore");
        skipFileList2.add(".ftl");
    }
}
