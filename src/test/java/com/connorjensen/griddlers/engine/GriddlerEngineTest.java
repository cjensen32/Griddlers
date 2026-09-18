package com.connorjensen.griddlers.engine;

import static com.connorjensen.griddlers.engine.GriddlerEngine.Layout;
import static com.connorjensen.griddlers.engine.GriddlerEngine.alternatingCells;
import static com.connorjensen.griddlers.engine.GriddlerEngine.bottomAlign;
import static com.connorjensen.griddlers.engine.GriddlerEngine.deriveClueList;
import static com.connorjensen.griddlers.engine.GriddlerEngine.drawBody;
import static com.connorjensen.griddlers.engine.GriddlerEngine.drawTopClues;
import static com.connorjensen.griddlers.engine.GriddlerEngine.measure;
import static com.connorjensen.griddlers.engine.GriddlerEngine.randomizeCells;
import static com.connorjensen.griddlers.engine.GriddlerEngine.render;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import com.connorjensen.griddlers.model.Cell;
import com.connorjensen.griddlers.model.Griddler;
import com.connorjensen.griddlers.model.Gutter;

public class GriddlerEngineTest {

  @Nested
  @DisplayName("nonRandomizeCells() and randomizeCells() tests")
  class CellConstruction {
    private static Stream<Arguments> randomizeSeedCases() {
      return Stream.of(
          arguments(1L, List.of(List.of(Cell.FILLED, Cell.EMPTY), List.of(Cell.EMPTY, Cell.EMPTY))),
          arguments(
              999L, List.of(List.of(Cell.FILLED, Cell.FILLED), List.of(Cell.FILLED, Cell.EMPTY))),
          arguments(
              581282250L,
              List.of(List.of(Cell.EMPTY, Cell.EMPTY), List.of(Cell.EMPTY, Cell.FILLED))),
          arguments(
              830269208L,
              List.of(List.of(Cell.EMPTY, Cell.FILLED), List.of(Cell.EMPTY, Cell.FILLED))),
          arguments(
              789544275L,
              List.of(List.of(Cell.FILLED, Cell.FILLED), List.of(Cell.FILLED, Cell.EMPTY))));
    }

    @ParameterizedTest(name = "{0} non-randomize correctly shapes rows/cols")
    @CsvSource({"1", "3", "4", "7", "11"})
    void nonRandomizeCellsAlternatesFillByRowAndColumn(int size) {
      List<List<Cell>> resultCells = alternatingCells(size, size);

      for (int i = 0; i < size; i++) {
        Cell rowVal = (i % 2 == 0) ? Cell.FILLED : Cell.EMPTY;
        assertEquals(rowVal, resultCells.getFirst().get(i));
        assertEquals(rowVal, resultCells.get(i).getFirst());
      }

      assertAll(
          () -> assertEquals(size, resultCells.size()),
          () -> assertEquals(Cell.FILLED, resultCells.getLast().getLast()),
          () -> assertEquals(size, resultCells.getFirst().size()));
    }

    @ParameterizedTest(name = "Random({0}) is deterministic for randomizeCells()")
    @MethodSource("randomizeSeedCases")
    void randomizeCellIsDeterministicForSameSeed(Long randSeedVal, List<List<Cell>> expectedCells) {
      Random random = new Random(randSeedVal);

      assertEquals(expectedCells, randomizeCells(2, random));
    }

    @ParameterizedTest(name = "Random({0}) returns square of requested size")
    @CsvSource({"8", "5", "1", "25"})
    void randomizeCellsReturnsSquareOfRequestedSize(int size) {
      Random random = new Random(1L);

      assertEquals(randomizeCells(size, random).size(), size);
    }
  }

  @Nested
  @DisplayName("clues() tests ")
  class ClueDerivation {
    private static Griddler SQUARE_GRIDDLER;
    private static Griddler NON_SQUARE_GRIDDLER;

    private static Stream<Arguments> deriveCluesCases() {
      return Stream.of(
          arguments(List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY), List.of()),
          arguments(List.of(Cell.FILLED, Cell.FILLED, Cell.FILLED), List.of(3)),
          arguments(List.of(Cell.FILLED, Cell.UNKNOWN, Cell.FILLED), List.of(1, 1)),
          arguments(List.of(Cell.FILLED, Cell.EMPTY, Cell.FILLED), List.of(1, 1)),
          arguments(List.of(Cell.FILLED, Cell.FILLED, Cell.EMPTY), List.of(2)),
          arguments(List.of(Cell.EMPTY, Cell.FILLED, Cell.FILLED), List.of(2)));
    }

    @BeforeEach
    void setUp() {
      List<List<Cell>> squareCells =
          List.of(
              List.of(Cell.EMPTY, Cell.FILLED, Cell.EMPTY, Cell.FILLED),
              List.of(Cell.EMPTY, Cell.EMPTY, Cell.FILLED, Cell.FILLED),
              List.of(Cell.EMPTY, Cell.FILLED, Cell.EMPTY, Cell.FILLED),
              List.of(Cell.EMPTY, Cell.FILLED, Cell.FILLED, Cell.EMPTY));
      SQUARE_GRIDDLER = new Griddler(squareCells);
      List<List<Cell>> nonSquareCells =
          List.of(
              List.of(Cell.EMPTY, Cell.FILLED, Cell.UNKNOWN, Cell.FILLED),
              List.of(Cell.FILLED, Cell.UNKNOWN, Cell.FILLED, Cell.FILLED),
              List.of(Cell.FILLED, Cell.FILLED, Cell.FILLED, Cell.FILLED),
              List.of(Cell.EMPTY, Cell.FILLED, Cell.FILLED, Cell.FILLED),
              List.of(Cell.EMPTY, Cell.FILLED, Cell.FILLED, Cell.FILLED),
              List.of(Cell.FILLED, Cell.EMPTY, Cell.UNKNOWN, Cell.EMPTY));
      NON_SQUARE_GRIDDLER = new Griddler(nonSquareCells);
    }

    @Test
    @DisplayName("Conversion of square griddler to gutters")
    void cluesDerivesRowAndColumnClues() {
      Gutter expectedGutters =
          new Gutter(
              List.of(List.of(1, 1), List.of(2), List.of(1, 1), List.of(2)),
              List.of(List.of(), List.of(1, 2), List.of(1, 1), List.of(3)));

      assertEquals(expectedGutters, GriddlerEngine.clues(SQUARE_GRIDDLER));
    }

    @Test
    @DisplayName("Conversion of non-square griddler to gutters")
    void cluesDerivesRowAndColumnCluesForNonSquareGriddler() {
      Gutter expectedGutters =
          new Gutter(
              List.of(List.of(1, 1), List.of(1, 2), List.of(4), List.of(3), List.of(3), List.of(1)),
              List.of(List.of(2, 1), List.of(1, 3), List.of(4), List.of(5)));

      assertEquals(expectedGutters, GriddlerEngine.clues(NON_SQUARE_GRIDDLER));
    }

    @ParameterizedTest(name = "deriveClueList({0}) -> {1}")
    @MethodSource("deriveCluesCases")
    void cluesDerivesLineClues(List<Cell> cells, List<Integer> expected) {
      assertEquals(expected, deriveClueList(cells));
    }
  }

  @Nested
  @DisplayName("measure() tests")
  class Measurement {
    private static Stream<Arguments> deriveMeasureWidth() {
      return Stream.of(
          arguments(
              "measure() keeps cellWidth minimum of 3 ",
              new Layout(0, 2, 3, 0),
              new Gutter(List.of(), List.of())),
          arguments(
              "measure() widens cellWidth for multi-digit clues",
              new Layout(0, 2, 5, 1),
              new Gutter(List.of(), List.of(List.of(10)))),
          arguments(
              "measure() widens gutterWidth for multi-digit clues",
              new Layout(1, 3, 3, 0),
              new Gutter(List.of(List.of(10)), List.of())),
          arguments(
              "measure() forces odd cellWidth",
              new Layout(0, 2, 5, 1),
              new Gutter(List.of(), List.of(List.of(100)))),
          arguments(
              "cellWidth = 7; is odd; >=1 padding on both sides",
              new Layout(0, 2, 7, 1),
              new Gutter(List.of(), List.of(List.of(1000)))));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("deriveMeasureWidth")
    void testMeasureWidthCalculation(String testName, Layout expected, Gutter gutter) {
      assertEquals(expected, measure(gutter));
    }

    private static Stream<Arguments> deriveMeasureCases() {
      return Stream.of(
          arguments(
              "xClueSize determined by longest rowClues list ",
              new Gutter(List.of(List.of(1), List.of(1, 1, 1, 1, 1, 1)), List.of()),
              new Layout(6, 2, 3, 0)),
          arguments(
              "yClueSize determined by longest columnClues list",
              new Gutter(List.of(), List.of(List.of(1), List.of(1, 1, 1, 1, 1, 1))),
              new Layout(0, 2, 3, 6)));
    }

    @ParameterizedTest(name = "x/y Clue size measurements are correct: {0}")
    @MethodSource("deriveMeasureCases")
    void testMeasureXYCalculation(String testName, Gutter gutter, Layout expected) {
      assertEquals(expected, measure(gutter));
    }
  }

  @Nested
  @DisplayName("bottomAlign() tests")
  class LanePadding {
    private static Stream<Arguments> paddingCases() {
      return Stream.of(
          arguments(
              "Returns exactly lane count entries", List.of("   ", "   ", " 1 "), List.of(1), 3, 3),
          arguments(
              "Returns all blank lanes for empty clues",
              List.of("   ", "   ", "   "),
              List.of(),
              3,
              3),
          arguments(
              "Fills every lane when clue count equals lane count",
              List.of(" 1 ", " 2 ", " 3 "),
              List.of(1, 2, 3),
              3,
              3),
          arguments(
              "Widens field for multi-digit clue",
              List.of("     ", " 10  ", "  5  "),
              List.of(10, 5),
              3,
              5));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("paddingCases")
    void testPaddingCases(
        String name, List<String> expected, List<Integer> clues, int laneCount, int clueWidth) {
      assertEquals(expected, bottomAlign(clues, laneCount, clueWidth));
    }
  }

  @Nested
  @DisplayName("drawTopClues() tests")
  class HeaderRendering {
    private static Stream<Arguments> drawTopCluesCases() {
      return Stream.of(
          arguments(
              "Emits one line per lane",
              """
              |
              |
              |
              """,
              new Layout(0, 0, 0, 3),
              List.of()),
          arguments(
              "Aligns each column over its grid column",
              """
              | 1 | 2 |
              """,
              new Layout(0, 0, 3, 1),
              List.of(List.of(1), List.of(2))),
          arguments(
              "Renders column with no clues",
              """
              |   | 2 |
              """,
              new Layout(0, 0, 3, 1),
              List.of(List.of(), List.of(2))),
          arguments(
              "Aligns each column over its grid column",
              """
                |
              """,
              new Layout(2, 1, 1, 1),
              List.of()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("drawTopCluesCases")
    void testTopCluesCases(
        String name, String expected, Layout layout, List<List<Integer>> columnClues) {
      assertEquals(expected, drawTopClues(layout, columnClues));
    }
  }

  @Nested
  @DisplayName("drawBody() tests")
  class BodyRendering {
    private static Stream<Arguments> drawBodyCases() {
      return Stream.of(
          arguments(
              "Emits border above every row and at bottom",
              """
              -+---+
               | # |
              -+---+
              """,
              new Layout(1, 1, 3, 0),
              List.of(List.of()),
              List.of(List.of(Cell.FILLED))),
          arguments(
              "Right aligns row clues in left gutter",
              """
              ----+---+
              1 1 | # |
              ----+---+
              """,
              new Layout(2, 2, 3, 0),
              List.of(List.of(1, 1)),
              List.of(List.of(Cell.FILLED))),
          arguments(
              "Renders correct with blank clues; renders non-square grid",
              """
              --+---+
              1 | # |
              --+---+
                | # |
              --+---+
              """,
              new Layout(1, 2, 3, 0),
              List.of(List.of(1), List.of()),
              List.of(List.of(Cell.FILLED), List.of(Cell.FILLED))));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("drawBodyCases")
    void testBodyCases(
        String name,
        String expected,
        Layout layout,
        List<List<Integer>> columnClues,
        List<List<Cell>> cells) {
      assertEquals(expected, drawBody(layout, columnClues, cells));
    }
  }

  @Nested
  @DisplayName("render() (Full process) tests")
  class FullRendering {

    private static Stream<Arguments> drawNonSquareCases() {
      return Stream.of(
          arguments(
              "4 x 6: Non Square Griddler matches Golden Output",
              """
              +---+---+---+---+
              | # | . | # | . |
              +---+---+---+---+
              | . | # | . | # |
              +---+---+---+---+
              | # | . | # | . |
              +---+---+---+---+
              | . | # | . | # |
              +---+---+---+---+
              | # | . | # | . |
              +---+---+---+---+
              | . | # | . | # |
              +---+---+---+---+
              """,
              alternatingCells(4, 6),
              new Gutter(blankGutter(6), blankGutter(4))),
          arguments(
              "8 x 7: Non Square Griddler matches Golden Output",
              """
              +---+---+---+---+---+---+---+---+
              | # | . | # | . | # | . | # | . |
              +---+---+---+---+---+---+---+---+
              | . | # | . | # | . | # | . | # |
              +---+---+---+---+---+---+---+---+
              | # | . | # | . | # | . | # | . |
              +---+---+---+---+---+---+---+---+
              | . | # | . | # | . | # | . | # |
              +---+---+---+---+---+---+---+---+
              | # | . | # | . | # | . | # | . |
              +---+---+---+---+---+---+---+---+
              | . | # | . | # | . | # | . | # |
              +---+---+---+---+---+---+---+---+
              | # | . | # | . | # | . | # | . |
              +---+---+---+---+---+---+---+---+
              """,
              alternatingCells(8, 7),
              new Gutter(blankGutter(8), blankGutter(7))));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("drawNonSquareCases")
    void renderNonSquareGriddler(
        String name, String expected, List<List<Cell>> cells, Gutter gutter) {
      assertEquals(expected, render(new Griddler(cells), gutter));
    }

    private static Stream<Arguments> drawSquareCases() {
      return Stream.of(
          arguments(
              "4 x 4: Square Griddler matches Golden Output",
              """
              +---+---+---+---+
              | # | . | # | . |
              +---+---+---+---+
              | . | # | . | # |
              +---+---+---+---+
              | # | . | # | . |
              +---+---+---+---+
              | . | # | . | # |
              +---+---+---+---+
              """,
              alternatingCells(4, 4),
              new Gutter(blankGutter(4), blankGutter(4))),
          arguments(
              "8 x 8: Square Griddler matches Golden Output",
              """
              +---+---+---+---+---+---+---+---+
              | # | . | # | . | # | . | # | . |
              +---+---+---+---+---+---+---+---+
              | . | # | . | # | . | # | . | # |
              +---+---+---+---+---+---+---+---+
              | # | . | # | . | # | . | # | . |
              +---+---+---+---+---+---+---+---+
              | . | # | . | # | . | # | . | # |
              +---+---+---+---+---+---+---+---+
              | # | . | # | . | # | . | # | . |
              +---+---+---+---+---+---+---+---+
              | . | # | . | # | . | # | . | # |
              +---+---+---+---+---+---+---+---+
              | # | . | # | . | # | . | # | . |
              +---+---+---+---+---+---+---+---+
              | . | # | . | # | . | # | . | # |
              +---+---+---+---+---+---+---+---+
              """,
              alternatingCells(8, 8),
              new Gutter(blankGutter(8), blankGutter(8))));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("drawSquareCases")
    void renderSquareGriddler(String name, String expected, List<List<Cell>> cells, Gutter gutter) {
      assertEquals(expected, render(new Griddler(cells), gutter));
    }

    private static List<List<Integer>> blankGutter(int size) {
      List<List<Integer>> blankGutter = new ArrayList<>(List.of());
      for (int i = 0; i < size; i++) {
        blankGutter.add(List.of());
      }
      return blankGutter;
    }
  }
}
