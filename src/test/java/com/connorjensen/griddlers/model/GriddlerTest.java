package com.connorjensen.griddlers.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GriddlerTest {
  // Distinct row values make swapped row and column indices detectable.
  private static final List<List<Cell>> INITIAL_CELLS =
      List.of(
          List.of(Cell.FILLED, Cell.FILLED, Cell.FILLED, Cell.FILLED),
          List.of(Cell.UNKNOWN, Cell.UNKNOWN, Cell.UNKNOWN, Cell.UNKNOWN),
          List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY),
          List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY));
  private static final int GRID_SIZE = 4;
  private Griddler grid;

  @BeforeEach
  void setUp() {
    grid = new Griddler(INITIAL_CELLS);
  }

  // 1. Construction and validation
  @Nested
  @DisplayName("1. Construction and validation")
  class ConstructionAndValidation {
    @Test
    void constructorPreservesCellValues() {
      assertEquals(INITIAL_CELLS, grid.getCells());
    }

    @Test
    @DisplayName("Constructor accepts 1 x 1 grid")
    void constructorAcceptsSmallestGrid() {
      List<List<Cell>> smallestGridPossible = List.of(List.of(Cell.EMPTY));
      Griddler smallestGriddler = new Griddler(smallestGridPossible);

      assertEquals(smallestGridPossible, smallestGriddler.getCells());
    }

    @Test
    void constructorRejectsNullRow() {
      List<List<Cell>> cellsWithNullRow =
          Arrays.asList(
              null,
              List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY),
              List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY));

      assertThrows(IllegalArgumentException.class, () -> new Griddler(cellsWithNullRow));
    }

    @Test
    @DisplayName("Constructor rejects empty rows")
    void constructorRejectsEmptyRows() {
      List<List<Cell>> emptyCells = List.of(List.of());
      assertThrows(IllegalArgumentException.class, () -> new Griddler(emptyCells));
    }

    @Test
    @DisplayName("Constructor rejects Ragged, Rectangular, & Uneven cells")
    void constructorRejectsRaggedRows() {
      List<List<Cell>> cellsWithRaggedRows =
          List.of(
              List.of(Cell.EMPTY, Cell.EMPTY),
              List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY),
              List.of(Cell.EMPTY, Cell.EMPTY));

      List<List<Cell>> cellsWithRectangularShape =
          List.of(
              List.of(Cell.EMPTY, Cell.EMPTY),
              List.of(Cell.EMPTY, Cell.EMPTY),
              List.of(Cell.EMPTY, Cell.EMPTY));

      List<List<Cell>> cellsWithUnevenShape =
          List.of(
              List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY),
              List.of(Cell.EMPTY, Cell.EMPTY),
              List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY));

      assertThrows(IllegalArgumentException.class, () -> new Griddler(cellsWithRaggedRows));
      assertThrows(IllegalArgumentException.class, () -> new Griddler(cellsWithRectangularShape));
      assertThrows(IllegalArgumentException.class, () -> new Griddler(cellsWithUnevenShape));
    }

    @Test
    void constructorRejectsNullCell() {
      List<List<Cell>> cellsWithNullCell =
          List.of(
              Arrays.asList(Cell.EMPTY, null, Cell.EMPTY),
              List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY),
              List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY));

      assertThrows(IllegalArgumentException.class, () -> new Griddler(cellsWithNullCell));
    }

    @Test
    void constructorRejectsEmptyCells() {
      List<List<Cell>> cellsWithEmptyShape = new ArrayList<>(List.of());

      assertThrows(IllegalArgumentException.class, () -> new Griddler(cellsWithEmptyShape));
    }

    @Test
    void constructorRejectsEmptyCellRow() {
      List<List<Cell>> cellsWithEmptyRow =
          List.of(List.of(Cell.EMPTY, Cell.FILLED), List.of(), List.of(Cell.FILLED, Cell.UNKNOWN));
      assertThrows(IllegalArgumentException.class, () -> new Griddler(cellsWithEmptyRow));
    }
  }

  @Nested
  @DisplayName("2. Dimensions and lookup")
  class DimensionsAndLookup {
    @Test
    void getSizeReturnsGridDimension() {
      assertEquals(GRID_SIZE, grid.getSize());
    }

    @Test
    void getCellsReturnsRowsWithExpectedSize() {
      List<List<Cell>> cells = grid.getCells();
      for (List<Cell> row : cells) {
        assertEquals(GRID_SIZE, row.size());
      }
    }

    @Test
    void getCellReturnsValueAtRequestedCoordinates() {
      assertEquals(Cell.FILLED, grid.getCell(0, 0));
      assertEquals(Cell.EMPTY, grid.getCell(3, 0));
    }

    @ParameterizedTest(name = "getCell rejects row={0}, column={1}")
    @CsvSource({
      "0, 20", "20, 0",
      "0, -1", "-1, 0",
      "0, 4", "4, 0"
    })
    void getCellRejectsInvalidCoordinates(int row, int column) {
      assertThrows(IndexOutOfBoundsException.class, () -> grid.getCell(row, column));
    }

    @Test
    void smallestGridSizeAndContent() {
      List<List<Cell>> smallestGridPossible = List.of(List.of(Cell.EMPTY));
      Griddler smallestGriddler = new Griddler(smallestGridPossible);

      assertEquals(Cell.EMPTY, smallestGriddler.getCell(0, 0));
      assertEquals(1, smallestGriddler.getSize());
    }
  }

  @Nested
  @DisplayName("3. Cell updates")
  class CellUpdates {
    @Test
    void setCellChangesOnlyRequestedCell() {
      List<List<Cell>> expectedCells =
          List.of(
              List.of(Cell.FILLED, Cell.FILLED, Cell.FILLED, Cell.FILLED),
              List.of(Cell.UNKNOWN, Cell.UNKNOWN, Cell.UNKNOWN, Cell.UNKNOWN),
              List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.UNKNOWN),
              List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY));

      grid.setCell(2, 3, Cell.UNKNOWN);

      assertEquals(Cell.UNKNOWN, grid.getCell(2, 3));
      assertEquals(expectedCells, grid.getCells());
    }

    @ParameterizedTest(name = "setCell rejects row={0}, column={1}")
    @CsvSource({
      "0, 20", "20, 0",
      "0, -1", "-1, 0",
      "0, 4", "4, 0"
    })
    void setCellRejectsInvalidCoordinates(int row, int column) {
      assertThrows(IndexOutOfBoundsException.class, () -> grid.setCell(row, column, Cell.UNKNOWN));
    }

    @Test
    void setCellRejectsNullCell() {
      assertThrows(IllegalArgumentException.class, () -> grid.setCell(0, 0, null));
    }
  }

  @Nested
  @DisplayName("4. Defensive copying and snapshots")
  class DefensiveCopyingAndSnapshots {
    @Test
    void modifyingReturnedCellsDoesNotChangeGrid() {
      List<List<Cell>> snapshot = grid.getCells();
      snapshot.get(0).set(0, Cell.UNKNOWN);
      snapshot.get(0).set(1, Cell.UNKNOWN);

      assertEquals(Cell.FILLED, grid.getCell(0, 0));
      assertEquals(Cell.FILLED, grid.getCell(0, 1));
    }
  }
}
