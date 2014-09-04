package com.wordnik.swagger.online;

import com.wordnik.swagger.codegen.*;
import com.wordnik.swagger.generator.util.*;
import com.wordnik.swagger.codegen.languages.*;

import com.wordnik.swagger.generator.model.*;
import com.wordnik.swagger.util.Json;

import java.util.*;
import java.io.*;

public class Generator {
  public static String generateClient(String language, ClientOptInput codegenInput) {
    if(codegenInput == null)
      return null;

    try {
      if(codegenInput.getOpts() == null)
        codegenInput.setOpts(new ClientOpts());

      File outputDir = getTmpFolder();

      CodegenConfig config = getConfig(language);
      config.setOutputDir(outputDir.getAbsolutePath());

      codegenInput.setConfig(config);

      new Codegen().opts(codegenInput).generate();

      ZipUtil zip = new ZipUtil();
      List<File> fileList = new ArrayList<File>();
      fileList.add(new File(outputDir.getAbsolutePath()));

      zip.compressFiles(fileList, outputDir + File.separator + "bundle.zip");
      return outputDir.getAbsolutePath() + File.separator + "bundle.zip";
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static List<InputOption> clientOptions(String language) {
    return null;
  }

  public static List<InputOption> serverOptions(String framework) {
    return null;
  }

  public static String generateServer(String framework, ClientOptInput opts) {
    return null;
  }

  static File getTmpFolder() throws IOException {
    File outputFolder = File.createTempFile("codegen-", "-tmp");
    outputFolder.delete();
    outputFolder.mkdir();
    outputFolder.deleteOnExit();

    new File(outputFolder.getAbsolutePath() + File.separator + "files").mkdir();
    String specFolder = outputFolder + File.separator + "specs";
    new File(specFolder).mkdir();
    return outputFolder;
  }

  static CodegenConfig getConfig(String name) {
    if("objc".equals(name))
      return new ObjcClientCodegen();
    else if("java".equals(name)) 
      return new JavaClientCodegen();
    else
      throw new RuntimeException("unsupported client type");
  }
}
