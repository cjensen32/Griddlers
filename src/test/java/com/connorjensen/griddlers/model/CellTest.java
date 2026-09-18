package com.connorjensen.griddlers.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

public class CellTest {

  @ParameterizedTest
  @EnumSource(Cell.class)
  void everyCellAsciiStrIsNonBlank(Cell cell) {
    assertFalse(cell.glyph().isBlank());
  }

  @Test
  void noCellSharesAsciiStr() {
    long distinctAsciiStr = Arrays.stream(Cell.values()).map(Cell::glyph).distinct().count();

    assertEquals(Cell.values().length, distinctAsciiStr);
  }

  @ParameterizedTest
  @CsvSource({"EMPTY, .", "FILLED, #", "UNKNOWN, x"})
  void everyCellCarriesExpectedAsciiStr(Cell cell, String expected) {
    assertEquals(expected, cell.glyph());
  }
}
