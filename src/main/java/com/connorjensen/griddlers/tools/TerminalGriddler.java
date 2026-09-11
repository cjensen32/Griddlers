package com.connorjensen.griddlers.tools;

import java.nio.file.Path;

import com.connorjensen.griddlers.engine.GriddlerEngine;

public class TerminalGriddler {
  private Path outputFile;
  private GriddlerEngine griddlerEngine;

  public TerminalGriddler() {
    this.outputFile = Path.of(System.getProperty("user.home"), "Downloads");
    this.griddlerEngine = new GriddlerEngine();
  }
}
