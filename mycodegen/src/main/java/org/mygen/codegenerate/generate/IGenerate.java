package org.mygen.codegenerate.generate;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.List;
import java.util.Map;

public interface IGenerate {
    Map<String, Object> loadConfigAndReadTable() throws Exception;

    List<String> generateCodeFile(String var1) throws Exception;

    List<String> generateCodeFile(String var1, String var2, String var3) throws Exception;
}
