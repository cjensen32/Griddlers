package com.connorjensen.griddlers.model;

import java.util.ArrayList;
import java.util.List;

public final class Griddler {
  private final int width;
  private final int height;
  private final List<List<Cell>> cells;

  public Griddler(List<List<Cell>> cells) {
    // Determine if shape can be used to determine height/width
    if (cells.isEmpty()) {
      throw new IllegalArgumentException("Griddler shape must have cells");
    }
    this.cells = new ArrayList<>();
    if (cells.getFirst() == null) {
      throw new IllegalArgumentException("Rows must not be null");
    }

    width = cells.getFirst().size(); // all rows should be this size
    for (List<Cell> row : cells) {
      if (row == null) {
        throw new IllegalArgumentException("Griddler rows must not be null");
      }
      if (row.isEmpty()) {
        throw new IllegalArgumentException("Griddler rows must have values");
      }
      if (row.size() != width) {
        throw new IllegalArgumentException("Griddler shape must be non-ragged");
      }
      for (Cell cell : row) {
        if (cell == null) {
          throw new IllegalArgumentException("Griddler cells must not be null");
        }
      }
      this.cells.add(new ArrayList<>(row));
    }
    this.height = cells.size();
  }

  private void checkBounds(int row, int column) {
    if (row < 0 || row >= height || column < 0 || column >= width) {
      throw new IndexOutOfBoundsException(
          "Griddler index: {" + row + ", " + column + "} - is out of bounds");
    }
  }

  public List<List<Cell>> getCells() {
    List<List<Cell>> cellsCopy = new ArrayList<>();
    for (List<Cell> row : cells) {
      List<Cell> newRow = List.copyOf(row);
      cellsCopy.add(new ArrayList<>(newRow));
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

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}
