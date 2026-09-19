package com.connorjensen.griddlers.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.connorjensen.griddlers.model.Cell;
import com.connorjensen.griddlers.model.Griddler;
import com.connorjensen.griddlers.model.Gutter;

public final class GriddlerEngine {
  private GriddlerEngine() {}

  record Layout(int xClueSize, int gutterWidth, int cellWidth, int yClueSize) {
    public int lGutterWidth() {
      return xClueSize * gutterWidth;
    }
  }

  public static List<List<Cell>> alternatingCells(int width, int height) {
    List<List<Cell>> cells = new ArrayList<>(height);

    // Create unrandomized cells
    for (int i = 0; i < height; i++) {
      List<Cell> newRow = new ArrayList<>();
      for (int j = 0; j < width; j++) {
        newRow.add(((i + j) % 2 == 0) ? Cell.FILLED : Cell.EMPTY);
      }
      cells.add(newRow);
    }
    return cells;
  }

  public static List<List<Cell>> randomizeCells(int size, Random random) {
    List<List<Cell>> cells = new ArrayList<>();

    List<Cell> randVals = List.of(Cell.EMPTY, Cell.FILLED);

    // Create randomized cells
    for (int i = 0; i < size; i++) {
      List<Cell> newRow = new ArrayList<>();
      for (int j = 0; j < size; j++) {
        newRow.add(randVals.get(random.nextInt(randVals.size())));
      }
      cells.add(newRow);
    }
    return cells;
  }

  public static Gutter clues(Griddler griddler) {
    List<List<Integer>> rowGutters = new ArrayList<>();
    List<List<Integer>> colGutters = new ArrayList<>();

    List<List<Cell>> cells = griddler.getCells();
    List<List<Cell>> columns = new ArrayList<>();

    for (int i = 0; i < griddler.getWidth(); i++) {
      List<Cell> newRow = new ArrayList<>();
      for (int j = 0; j < griddler.getHeight(); j++) {
        newRow.add(cells.get(j).get(i));
      }
      columns.add(newRow);
    }

    for (List<Cell> cellRow : cells) {
      rowGutters.add(deriveClueList(cellRow));
    }
    for (List<Cell> cellColumn : columns) {
      colGutters.add(deriveClueList(cellColumn));
    }

    return new Gutter(rowGutters, colGutters);
  }

  static List<Integer> deriveClueList(List<Cell> run) {
    List<Integer> runClues = new ArrayList<>(run.size());
    int streak = 0;

    for (Cell cell : run) {
      if (cell != Cell.FILLED) {
        if (streak >= 1) {
          runClues.add(streak);
        }
        streak = 0;
        continue;
      }
      streak++;
    }

    if (streak >= 1) {
      runClues.add(streak);
    }

    return runClues;
  }

  // String output methods
  public static String render(Griddler griddler, Gutter gutters) {
    Layout layout = measure(gutters);

    return drawTopClues(layout, gutters.columnClues())
        + drawBody(layout, gutters.rowClues(), griddler.getCells());
  }

  static Layout measure(Gutter gutter) {
    int xClueSize = 0;
    int xClueMaxWidth = 1;
    for (List<Integer> xGutters : gutter.rowClues()) {
      if (xGutters.size() > xClueSize) {
        xClueSize = xGutters.size();
      }

      for (int clue : xGutters) {
        xClueMaxWidth = Math.max(xClueMaxWidth, String.valueOf(clue).length());
      }
    }

    int yClueSize = 0;
    int yClueMaxWidth = 1;
    for (List<Integer> yGutters : gutter.columnClues()) {
      if (yGutters.size() > yClueSize) {
        yClueSize = yGutters.size();
      }
      for (int clue : yGutters) {
        yClueMaxWidth = Math.max(yClueMaxWidth, String.valueOf(clue).length());
      }
    }

    int cellWidth = Math.max(3, yClueMaxWidth + 2);
    if (cellWidth % 2 == 0) {
      ++cellWidth;
    }

    int gutterWidth = xClueMaxWidth + 1;

    return new Layout(xClueSize, gutterWidth, cellWidth, yClueSize);
  }

  static String drawTopClues(Layout layout, List<List<Integer>> columnClues) {
    StringBuilder sb = new StringBuilder();

    List<List<String>> offsetTopClues = new ArrayList<>(layout.yClueSize);

    for (List<Integer> colClues : columnClues) {
      offsetTopClues.add(bottomAlign(colClues, layout.yClueSize, layout.cellWidth()));
    }

    for (int i = 0; i < layout.yClueSize(); i++) {
      sb.repeat(" ", layout.lGutterWidth());
      sb.append("|");
      for (List<String> offsetTopClue : offsetTopClues) {
        sb.append(offsetTopClue.get(i));
        sb.append("|");
      }
      sb.append("\n");
    }

    return sb.toString();
  }

  static String drawBody(Layout layout, List<List<Integer>> rowClues, List<List<Cell>> cells) {
    StringBuilder sb = new StringBuilder();
    List<String> rowClueStrings = new ArrayList<>();

    for (List<Integer> clues : rowClues) {
      List<String> bottomAlignedStrings = bottomAlign(clues, layout.xClueSize, layout.gutterWidth);

      rowClueStrings.add(String.join("", bottomAlignedStrings));
    }

    for (int i = 0; i < cells.size(); i++) {
      List<Cell> row = cells.get(i);

      sb.append(rowSeparator(cells.getFirst().size(), layout.cellWidth(), layout.lGutterWidth()));

      sb.append(rowClueStrings.get(i));
      sb.append("|");

      for (Cell cell : row) {
        sb.append(center(cell.glyph(), layout.cellWidth()));
        sb.append("|");
      }
      sb.append("\n");
    }
    sb.append(rowSeparator(cells.getFirst().size(), layout.cellWidth(), layout.lGutterWidth()));

    return sb.toString();
  }

  static List<String> bottomAlign(List<Integer> clues, int laneCount, int clueWidth) {
    List<String> offsetVals = new ArrayList<>(laneCount);

    for (int i = 0; i < laneCount; i++) {
      if (i >= clues.size()) {
        offsetVals.addFirst(center(" ", clueWidth));
        continue;
      }
      String offsetVal = center(String.valueOf(clues.get(i)), clueWidth);
      offsetVals.add(offsetVal);
    }
    return offsetVals;
  }

  // Utilities
  private static String center(String value, int width) {
    int spaces = width - value.length();

    int leftSpaces = spaces / 2;
    int rightSpaces = spaces - leftSpaces;

    return " ".repeat(leftSpaces) + value + " ".repeat(rightSpaces);
  }

  private static String rowSeparator(int columns, int cellWidth, int lGutterWidth) {
    StringBuilder sb = new StringBuilder();
    sb.repeat("-", lGutterWidth);
    sb.repeat("+" + "-".repeat(cellWidth), columns);
    sb.append("+\n");

    return sb.toString();
  }
}
