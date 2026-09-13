package com.connorjensen.griddlers.engine;

import java.util.List;

import com.connorjensen.griddlers.model.Cell;
import com.connorjensen.griddlers.model.Griddler;

public class GriddlerEngine {
  private GriddlerEngine() {}

  public static String render(Griddler griddler) {
    StringBuilder griddlerString = new StringBuilder();
    for (List<Cell> row : griddler.getCells()) {
      for (Cell cell : row) {
        griddlerString.append(cell.asciiStr());
      }
      griddlerString.append("\n");
    }
    return griddlerString.toString();
  }
}
