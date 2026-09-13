package com.connorjensen.griddlers.model;

public enum Cell {
  EMPTY("."),
  FILLED("#"),
  UNKNOWN("x");

  private final String asciiStr;

  Cell(String asciiStr) {
    this.asciiStr = asciiStr;
  }

  public String asciiStr() {
    return this.asciiStr;
  }
}
