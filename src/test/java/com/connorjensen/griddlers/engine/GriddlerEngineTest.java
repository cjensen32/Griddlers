package com.connorjensen.griddlers.engine;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.connorjensen.griddlers.model.Cell;
import com.connorjensen.griddlers.model.Griddler;
import com.connorjensen.griddlers.model.Gutter;

public class GriddlerEngineTest {
  private static final List<List<Cell>> CELLS =
      List.of(
          List.of(Cell.EMPTY, Cell.FILLED, Cell.UNKNOWN, Cell.FILLED),
          List.of(Cell.FILLED, Cell.UNKNOWN, Cell.FILLED, Cell.FILLED),
          List.of(Cell.FILLED, Cell.FILLED, Cell.FILLED, Cell.FILLED),
          List.of(Cell.EMPTY, Cell.EMPTY, Cell.UNKNOWN, Cell.EMPTY));
  private static final Griddler GRIDDLER = new Griddler(CELLS);
  private static final Gutter GUTTERS = GriddlerEngine.gutters(GRIDDLER);

  @Test
  @DisplayName("Test conversion to string using `render` method")
  void testConversionToString() {

    String expected =
        """
        +---+---+---+---+
        | . | # | x | # |
        +---+---+---+---+
        | # | x | # | # |
        +---+---+---+---+
        | # | # | # | # |
        +---+---+---+---+
        | . | . | x | . |
        +---+---+---+---+
        """;

    assertEquals(expected, GriddlerEngine.render(GRIDDLER));
  }

  @Test
  @DisplayName("Test conversion to string using `renderWithGutters` method")
  void testConversionToStringWithGutters() {

    String expected =
        """
            |   | 1 |   |   |
            | 2 | 1 | 2 | 3 |
        ----+---+---+---+---+
        1 1 | . | # | x | # |
        ----+---+---+---+---+
        1 2 | # | x | # | # |
        ----+---+---+---+---+
          4 | # | # | # | # |
        ----+---+---+---+---+
            | . | . | x | . |
        ----+---+---+---+---+
        """;

    assertEquals(expected, GriddlerEngine.renderWithGutters(GRIDDLER, GUTTERS));
  }

  @Test
  @DisplayName("`10` x and y gutters `renderWithGutters` test")
  void testConversionToStringWithLargeXYGutters() {
    // for this test readability, going to map Cell vals to local objects
    Cell a = Cell.EMPTY;
    Cell b = Cell.FILLED;
    Cell c = Cell.UNKNOWN;
    List<List<Cell>> extremelyLargeXYCells =
        List.of(
            List.of(a, b, a, b, a, b, a, b, a, b),
            List.of(b, b, b, b, b, b, b, b, b, b),
            List.of(a, b, a, a, b, c, a, b, c, a),
            List.of(b, b, b, a, b, c, a, b, c, a),
            List.of(a, b, a, a, c, a, a, a, c, a),
            List.of(b, b, b, a, b, c, a, b, c, a),
            List.of(a, b, a, a, b, c, a, b, c, a),
            List.of(b, b, b, a, b, c, a, a, c, a),
            List.of(a, b, a, a, a, c, b, b, c, a),
            List.of(b, b, b, a, b, c, a, b, c, a));
    Griddler extretremelyLargeXYGriddler = new Griddler(extremelyLargeXYCells);
    Gutter extremelyLargeXYGutters = GriddlerEngine.gutters(extretremelyLargeXYGriddler);
    String expected =
        """
                       |  1  |     |  1  |     |     |     |     |     |     |     |
                       |  1  |     |  1  |     |     |     |     |     |     |     |
                       |  1  |     |  1  |     |  3  |     |     |  4  |     |     |
                       |  1  |     |  1  |     |  3  |     |  1  |  2  |     |     |
                       |  1  | 10  |  1  |  2  |  1  |  2  |  1  |  2  |  1  |  2  |
        ---------------+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
         1  1  1  1  1 |  .  |  #  |  .  |  #  |  .  |  #  |  .  |  #  |  .  |  #  |
        ---------------+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
                    10 |  #  |  #  |  #  |  #  |  #  |  #  |  #  |  #  |  #  |  #  |
        ---------------+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
               1  1  1 |  .  |  #  |  .  |  .  |  #  |  x  |  .  |  #  |  x  |  .  |
        ---------------+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
               3  1  1 |  #  |  #  |  #  |  .  |  #  |  x  |  .  |  #  |  x  |  .  |
        ---------------+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
                     1 |  .  |  #  |  .  |  .  |  x  |  .  |  .  |  .  |  x  |  .  |
        ---------------+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
               3  1  1 |  #  |  #  |  #  |  .  |  #  |  x  |  .  |  #  |  x  |  .  |
        ---------------+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
               1  1  1 |  .  |  #  |  .  |  .  |  #  |  x  |  .  |  #  |  x  |  .  |
        ---------------+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
                  3  1 |  #  |  #  |  #  |  .  |  #  |  x  |  .  |  .  |  x  |  .  |
        ---------------+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
                  1  2 |  .  |  #  |  .  |  .  |  .  |  x  |  #  |  #  |  x  |  .  |
        ---------------+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
               3  1  1 |  #  |  #  |  #  |  .  |  #  |  x  |  .  |  #  |  x  |  .  |
        ---------------+-----+-----+-----+-----+-----+-----+-----+-----+-----+-----+
        """;

    assertEquals(
        expected,
        GriddlerEngine.renderWithGutters(extretremelyLargeXYGriddler, extremelyLargeXYGutters));
  }

  @Test
  @DisplayName("Test conversion of griddler to gutters")
  void convertGriddlerToGuttersAccurately() {
    Gutter expectedGutters =
        new Gutter(
            List.of(List.of(1, 1), List.of(1, 2), List.of(4), List.of()),
            List.of(List.of(2), List.of(1, 1), List.of(2), List.of(3)));

    assertEquals(expectedGutters, GriddlerEngine.gutters(GRIDDLER));
  }

  @Test
  @DisplayName("Test non-randomized cell creation")
  void createCellGridUsingNonRandomMethod() {
    int size = 4;
    List<List<Cell>> expectedCells =
        List.of(
            List.of(Cell.FILLED, Cell.EMPTY, Cell.FILLED, Cell.EMPTY),
            List.of(Cell.FILLED, Cell.EMPTY, Cell.FILLED, Cell.EMPTY),
            List.of(Cell.FILLED, Cell.EMPTY, Cell.FILLED, Cell.EMPTY),
            List.of(Cell.FILLED, Cell.EMPTY, Cell.FILLED, Cell.EMPTY));

    assertEquals(expectedCells, GriddlerEngine.nonRandomizeCells(size));
  }

  @ParameterizedTest
  @DisplayName("Test randomized cell creation")
  @CsvSource({"5", "6", "10", "1000"})
  void createCellGridUsingRandomMethod(int size) {
    List<List<Cell>> cells = GriddlerEngine.randomizeCells(size);
    int actualCount = cells.stream().mapToInt(List::size).sum();

    assertAll(() -> assertEquals(size * size, actualCount), () -> assertEquals(size, cells.size()));
  }
}
