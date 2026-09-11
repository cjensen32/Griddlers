package com.connorjensen.griddlers;

import com.connorjensen.griddlers.model.Griddler;

public class GriddlerEngine {
  private Long size;
  private String engineName;

  public GriddlerEngine(Long size, String engineName) {
    this.size = size;
    this.engineName = engineName;
  }

  public GriddlerEngine() {
    this.size = 10L;
    this.engineName = "Griddler Engine";
  }

  public Griddler makeGriddler() {
    return new Griddler(this.size);
  }
}