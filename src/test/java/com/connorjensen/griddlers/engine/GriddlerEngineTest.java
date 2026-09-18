package com.connorjensen.griddlers.engine;

import static com.connorjensen.griddlers.engine.GriddlerEngine.bottomAlign;
import static com.connorjensen.griddlers.engine.GriddlerEngine.deriveClueList;
import static com.connorjensen.griddlers.engine.GriddlerEngine.measure;
import static com.connorjensen.griddlers.engine.GriddlerEngine.nonRandomizeCells;
import static com.connorjensen.griddlers.engine.GriddlerEngine.randomizeCells;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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
      List<List<Cell>> resultCells = nonRandomizeCells(size);

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
              new Gutter(List.of(), List.of()),
              new GriddlerEngine.Layout(0, 2, 3, 0)),
          arguments(
              "measure() widens cellWidth for multi-digit clues",
              new Gutter(List.of(), List.of(List.of(10))),
              new GriddlerEngine.Layout(0, 2, 5, 1)),
          arguments(
              "measure() widens gutterWidth for multi-digit clues",
              new Gutter(List.of(List.of(10)), List.of()),
              new GriddlerEngine.Layout(1, 3, 3, 0)),
          arguments(
              "measure() forces odd cellWidth",
              new Gutter(List.of(), List.of(List.of(100))),
              new GriddlerEngine.Layout(0, 2, 5, 1)),
          arguments(
              "cellWidth = 7; is odd; >=1 padding on both sides",
              new Gutter(List.of(), List.of(List.of(1000))),
              new GriddlerEngine.Layout(0, 2, 7, 1)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("deriveMeasureWidth")
    void testMeasureWidthCalculation(
        String testName, Gutter gutter, GriddlerEngine.Layout expected) {
      assertEquals(expected, measure(gutter));
    }

    private static Stream<Arguments> deriveMeasureCases() {
      return Stream.of(
          arguments(
              "xClueSize determined by longest rowClues list ",
              new Gutter(List.of(List.of(1), List.of(1, 1, 1, 1, 1, 1)), List.of()),
              new GriddlerEngine.Layout(6, 2, 3, 0)),
          arguments(
              "yClueSize determined by longest columnClues list",
              new Gutter(List.of(), List.of(List.of(1), List.of(1, 1, 1, 1, 1, 1))),
              new GriddlerEngine.Layout(0, 2, 3, 6)));
    }

    @ParameterizedTest(name = "x/y Clue size measurements are correct: {0}")
    @MethodSource("deriveMeasureCases")
    void testMeasureXYCalculation(String testName, Gutter gutter, GriddlerEngine.Layout expected) {
      assertEquals(expected, measure(gutter));
    }
  }

  @Nested
  @DisplayName("bottomAlign() tests")
  class LanePadding {
    private static Stream<Arguments> paddingCases() {
      return Stream.of(
        arguments(List.of(1), 3, 3, List.of("   ", "   ", " 1 ")),
        arguments(List.of(), 3, 1, List.of(" ", " ", " ")),
        arguments(List.of(1, 2, 3), 3, 3, List.of(" 1 ", " 2 ", " 3 "))
      );
    }

    @ParameterizedTest
    @MethodSource("paddingCases")
    void testPaddingCases(List<Integer> clues, int laneCount, int clueWidth, List<String> expected) {
      assertEquals(expected, bottomAlign(clues, laneCount, clueWidth));
    }
  }

  @Nested
  @DisplayName("Header Rendering tests")
  class HeaderRendering {}

  @Nested
  @DisplayName("Body Rendering tests")
  class BodyRendering {}

  @Nested
  @DisplayName("Full Rendering tests")
  class FullRendering {}
}
