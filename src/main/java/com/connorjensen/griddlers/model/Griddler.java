package com.connorjensen.griddlers.model;

import java.util.ArrayList;
import java.util.List;

public final class Griddler {
  private final int size;
  private final List<List<Cell>> cells;

  public Griddler(List<List<Cell>> cells) {
    // Determine if shape can be used to determine height/width
    if (cells.isEmpty()) {
      throw new IllegalArgumentException("Griddler shape must have cells");
    }
    this.cells = new ArrayList<>();
    for (List<Cell> row : cells) {
      if (row == null) {
        throw new IllegalArgumentException("Griddler rows must not be null");
      }
      if (row.isEmpty()) {
        throw new IllegalArgumentException("Griddler rows must have values");
      }
      if (row.size() != cells.size()) {
        throw new IllegalArgumentException("Griddler shape must be square");
      }
      for (Cell cell : row) {
        if (cell == null) {
          throw new IllegalArgumentException("Griddler cells must not be null");
        }
      }
      this.cells.add(new ArrayList<>(row));
    }
    this.size = cells.size();
  }

  private void checkBounds(int row, int column) {
    if (row < 0 || row >= size || column < 0 || column >= size) {
      throw new IndexOutOfBoundsException(
          "Griddler index: {" + row + ", " + column + "} - is out of bounds");
    }
  }

  public List<List<Cell>> getCells() {
    List<List<Cell>> cellsCopy = new ArrayList<>();
    for (List<Cell> row : cells) {
      cellsCopy.add(new ArrayList<>(row));
    }
    return List.copyOf(cellsCopy);
  }

  public Cell getCell(int row, int column) {
    checkBounds(row, column);
    return this.cells.get(row).get(column);
  }

  public void setCell(int row, int column, Cell cell) {
    if (cell == null) {
      throw new IllegalArgumentException("Cell value must not be null");
    }
    checkBounds(row, column);
    this.cells.get(row).set(column, cell);
  }

  public int getSize() {
    return this.size;
  }
}
