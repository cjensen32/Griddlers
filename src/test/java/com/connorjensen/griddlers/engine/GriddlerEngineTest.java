package com.connorjensen.griddlers.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.connorjensen.griddlers.model.Cell;
import com.connorjensen.griddlers.model.Griddler;

public class GriddlerEngineTest {
  private static final List<List<Cell>> INITIAL_CELLS =
      List.of(
          List.of(Cell.FILLED, Cell.FILLED, Cell.FILLED),
          List.of(Cell.UNKNOWN, Cell.UNKNOWN, Cell.UNKNOWN),
          List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY));
  private static final Griddler INITIAL_GRIDDLER = new Griddler(INITIAL_CELLS);

  @Test
  @DisplayName("Test conversion to string")
  void testConversionToString() {

    StringBuilder expected = new StringBuilder();

    for (List<Cell> rows : INITIAL_CELLS) {
      for (Cell cell : rows) {
        expected.append(cell.asciiStr());
      }
      expected.append("\n");
    }

    assertEquals(expected.toString(), GriddlerEngine.render(INITIAL_GRIDDLER));
  }
}
