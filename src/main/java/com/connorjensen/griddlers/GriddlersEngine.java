package com.connorjensen.griddlers;

import java.nio.file.Path;

import com.connorjensen.griddlers.model.Griddler;

public class GriddlersEngine {
  private Long size;
  private String engineName;
  private Path outputFile;

  public GriddlersEngine(Long size, String engineName, Path outputFile) {
    this.size = size;
    this.engineName = engineName;
    this.outputFile = outputFile;
  }

  public GriddlersEngine() {
    this.size = 10L;
    this.engineName = "Griddler Engine";
    this.outputFile = Path.of(System.getProperty("user.home"), "Downloads");
  }

  public Griddler makeGriddler() {
    return new Griddler();
  }
}
