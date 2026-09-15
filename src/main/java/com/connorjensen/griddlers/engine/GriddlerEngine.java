package com.connorjensen.griddlers.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.connorjensen.griddlers.model.Cell;
import com.connorjensen.griddlers.model.Griddler;
import com.connorjensen.griddlers.model.Gutter;

public class GriddlerEngine {
  private GriddlerEngine() {}

  public static Gutter gutters(Griddler griddler) {
    int size = griddler.getSize();
    List<List<Cell>> cells = griddler.getCells();
    List<List<Integer>> xAxisGutters = new ArrayList<>();
    List<List<Integer>> yAxisGutters = new ArrayList<>();
    for (int x = 0; x < size; x++) {
      yAxisGutters.add(new ArrayList<>());
    }
    List<Integer> yFillStreaks = new ArrayList<>(Collections.nCopies(size, 0));

    for (int i = 0; i < size; i++) {
      List<Cell> cellRow = cells.get(i);
      List<Integer> xGutterRow = new ArrayList<>();
      int xFillStreak = 0;

      for (int j = 0; j < cellRow.size(); j++) {
        Cell cell = cellRow.get(j);

        if (cell == Cell.FILLED) {
          xFillStreak += 1;
          yFillStreaks.set(j, yFillStreaks.get(j) + 1);
        } else {
          if (xFillStreak > 0) {
            /* reset x pointer */
            xGutterRow.add(xFillStreak);
            xFillStreak = 0;
          }
          if (yFillStreaks.get(j) > 0) {
            yAxisGutters.get(j).add(yFillStreaks.get(j));
            yFillStreaks.set(j, 0);
          }
        }
      }
      if (xFillStreak > 0) {
        xGutterRow.add(xFillStreak);
      }
      xAxisGutters.add(xGutterRow);
    }

    for (int k = 0; k < size; k++) {
      if (yFillStreaks.get(k) > 0) {
        yAxisGutters.get(k).add(yFillStreaks.get(k));
      }
    }

    return new Gutter(xAxisGutters, yAxisGutters);
  }

  public static String render(Griddler griddler) {
    int size = griddler.getSize();
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < size; i++) {
      List<Cell> row = griddler.getCells().get(i);
      sb.append(renderXBorder(size, 3));
      sb.append("|");

      for (Cell cell : row) {
        sb.append(center(cell.asciiStr(), 3));
        sb.append("|");
      }
      sb.append("\n");
    }
    sb.append(renderXBorder(size, 3));

    return sb.toString();
  }

  public static String renderWithGutters(Griddler griddler, Gutter gutters) {
    StringBuilder sb = new StringBuilder();
    int size = griddler.getSize();

    int yClueSize = 0;
    int yClueMaxWidth = 1;
    for (List<Integer> yGutters : gutters.yAxisGutters()) {
      if (yGutters.size() > yClueSize) {
        yClueSize = yGutters.size();
      }
      for (int clue : yGutters) {
        yClueMaxWidth = Math.max(yClueMaxWidth, String.valueOf(clue).length());
      }
    }

    int xClueSize = 0;
    int xClueMaxWidth = 1;
    for (List<Integer> xGutters : gutters.xAxisGutters()) {
      if (xGutters.size() > xClueSize) {
        xClueSize = xGutters.size();
      }

      for (int clue : xGutters) {
        xClueMaxWidth = Math.max(xClueMaxWidth, String.valueOf(clue).length());
      }
    }

    int cellWidth = Math.max(3, yClueMaxWidth + 2);
    if (cellWidth % 2 == 0) {
      ++cellWidth;
    }

    int gutterWidth = xClueMaxWidth + 1;
    int leftGutterWidth = xClueSize * gutterWidth;

    for (int yClueIndex = yClueSize - 1; yClueIndex >= 0; yClueIndex--) {
      sb.repeat(" ", leftGutterWidth);

      for (int yColumn = 0; yColumn < size; yColumn++) {
        List<Integer> yClueColumn = gutters.yAxisGutters().get(yColumn).reversed();
        sb.append("|");

        if (yClueIndex < yClueColumn.size()) {
          String clue = String.valueOf(yClueColumn.get(yClueIndex));
          sb.append(center(clue, cellWidth));
        } else {
          sb.repeat(" ", cellWidth);
        }
      }
      sb.append("|\n");
    }

    // X-axis clues + griddler body
    for (int rowIndex = 0; rowIndex < size; rowIndex++) {
      List<Cell> row = griddler.getCells().get(rowIndex);
      List<Integer> xRowClues = gutters.xAxisGutters().get(rowIndex).reversed();

      // Border
      sb.repeat("-", leftGutterWidth);
      sb.append(renderXBorder(size, cellWidth));

      // X-axis clues
      for (int j = xClueSize - 1; j >= 0; j--) {
        if (j < xRowClues.size()) {
          String xClue = String.valueOf(xRowClues.get(j));

          sb.repeat(" ", gutterWidth - xClue.length() - 1);
          sb.append(xClue);
          sb.append(" ");
        } else {
          sb.repeat(" ", gutterWidth);
        }
      }
      sb.append("|");

      for (Cell cell : row) {
        sb.append(center(cell.asciiStr(), cellWidth));
        sb.append("|");
      }
      sb.append("\n");
    }

    // Bottom row
    sb.repeat("-", leftGutterWidth);
    sb.append(renderXBorder(size, cellWidth));

    return sb.toString();
  }

  // Utilities
  private static String renderXBorder(int size, int cellWidth) {
    return ("+" + "-".repeat(cellWidth)).repeat(size) + "+\n";
  }

  private static String center(String value, int width) {
    int spaces = width - value.length();

    int leftSpaces = spaces / 2;
    int rightSpaces = spaces - leftSpaces;

    return " ".repeat(leftSpaces) + value + " ".repeat(rightSpaces);
  }

  public static List<List<Cell>> nonRandomizeCells(int size) {
    List<List<Cell>> cells = new ArrayList<>();

    // Create unrandomized cells
    for (int i = 0; i < size; i++) {
      List<Cell> newRow = new ArrayList<>();
      for (int j = 0; j < size; j++) {
        newRow.add((j % 2 == 0) ? Cell.FILLED : Cell.EMPTY);
      }
      cells.add(newRow);
    }
    return cells;
  }

  public static List<List<Cell>> randomizeCells(int size) {
    List<List<Cell>> cells = new ArrayList<>();

    Random random = new Random();
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
}
