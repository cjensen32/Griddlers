package com.connorjensen.griddlers.model;

public enum Cell {
  EMPTY("."),
  FILLED("#"),
  UNKNOWN("x");

  private final String glyph;

  Cell(String glyph) {
    this.glyph = glyph;
  }

  public String asciiStr() {
    return this.glyph;
  }
}
