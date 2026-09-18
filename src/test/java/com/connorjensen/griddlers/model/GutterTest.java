package com.connorjensen.griddlers.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Gutter record")
class GutterTest {

  private List<List<Integer>> rowClues;
  private List<List<Integer>> columnClues;
  private Gutter expected;

  @BeforeEach
  void setUp() {
    rowClues =
        new ArrayList<>(
            Arrays.asList(
                Arrays.asList(1, 2, 1), Arrays.asList(1), Arrays.asList(2, 2), Arrays.asList(2)));
    columnClues =
        new ArrayList<>(
            Arrays.asList(
                Arrays.asList(1, 2), Arrays.asList(1), Arrays.asList(1, 1), Arrays.asList(1)));
    expected = new Gutter(rowClues, columnClues);
  }

  @Nested
  @DisplayName("Construction tests")
  class Construction {
    @Test
    @DisplayName("stores row clues with the same values as the input")
    void storesRowCluesWithSameValues() {
      assertEquals(expected.rowClues(), rowClues);
    }

    @Test
    @DisplayName("stores column clues with the same values as the input")
    void storesColumnCluesWithSameValues() {
      assertEquals(expected.columnClues(), columnClues);
    }

    @Test
    @DisplayName("accepts empty outer lists")
    void acceptsEmptyOuterLists() {
      Gutter recieved = new Gutter(rowClues, List.of());

      assertAll(
          () -> assertEquals(rowClues, recieved.rowClues()),
          () -> assertEquals(List.of(), recieved.columnClues()));
    }

    @Test
    @DisplayName("accepts empty inner lists (e.g. a blank row)")
    void acceptsEmptyInnerLists() {
      List<List<Integer>> emptyInnerRowClues = List.of(List.of(), List.of(2));
      List<List<Integer>> emptyInnerColumnClues = List.of(List.of(1), List.of());

      Gutter recieved = new Gutter(emptyInnerRowClues, emptyInnerColumnClues);
      assertAll(
          () -> assertEquals(emptyInnerRowClues, recieved.rowClues()),
          () -> assertEquals(emptyInnerColumnClues, recieved.columnClues()));
    }

    @Test
    @DisplayName("drops null inner lists")
    void dropsNullInnerLists() {
      List<List<Integer>> nullInnerRowClues = new ArrayList<>(Arrays.asList(null, List.of(2)));
      List<List<Integer>> nullInnerColumnClues = new ArrayList<>(Arrays.asList(List.of(1), null));

      Gutter recieved = new Gutter(nullInnerRowClues, nullInnerColumnClues);
      assertAll(
          () -> assertEquals(List.of(List.of(2)), recieved.rowClues()),
          () -> assertEquals(List.of(List.of(1)), recieved.columnClues()));
    }

    @Test
    @DisplayName("throws NullPointerException when rowClues is null")
    void throwsWhenRowCluesNull() {
      assertThrows(NullPointerException.class, () -> new Gutter(null, List.of(List.of())));
    }

    @Test
    @DisplayName("throws NullPointerException when columnClues is null")
    void throwsWhenColumnCluesNull() {
      assertThrows(NullPointerException.class, () -> new Gutter(List.of(List.of()), null));
    }
  }

  @Nested
  @DisplayName("Defensive copying tests")
  class DefensiveCopy {

    @Test
    @DisplayName("adding to the original outer list does not change the record")
    void outerListMutationDoesNotLeak() {
      rowClues.addFirst(List.of(1, 1, 1, 1));
      columnClues.add(List.of(1, 1, 1, 1));

      assertAll(
          () -> assertNotEquals(rowClues, expected.rowClues()),
          () -> assertNotEquals(columnClues, expected.columnClues()));
    }

    @Test
    @DisplayName("changing an original inner list does not change the record")
    void innerListMutationDoesNotLeak() {
      rowClues.set(0, List.of(1, 1, 1, 1));
      rowClues.getLast().set(0, 5);
      columnClues.set(0, List.of(1, 1, 1, 1));
      columnClues.getLast().set(0, 5);

      assertAll(
          () -> assertNotEquals(rowClues, expected.rowClues()),
          () -> assertNotEquals(columnClues, expected.columnClues()));
    }

    @Test
    @DisplayName("stored inner/outer lists are not the same instances")
    void storedListsAreNewInstances() {
      assertAll(
          () -> assertNotSame(rowClues, expected.rowClues()),
          () -> assertNotSame(rowClues.getFirst(), expected.rowClues().getFirst()),
          () -> assertNotSame(columnClues, expected.columnClues()),
          () -> assertNotSame(columnClues.getFirst(), expected.columnClues().getFirst()));
    }
  }

  @Nested
  @DisplayName("Immutability of accessors")
  class Immutability {

    @Test
    @DisplayName("rowClues() outer list is unmodifiable")
    void rowCluesOuterUnmodifiable() {
      assertAll(
          () ->
              assertThrows(
                  UnsupportedOperationException.class,
                  () -> expected.rowClues().set(0, List.of(1, 2, 3))),
          () ->
              assertThrows(
                  UnsupportedOperationException.class,
                  () -> expected.rowClues().add(List.of(1, 1, 1, 1))));
    }

    @Test
    @DisplayName("rowClues() inner lists are unmodifiable")
    void rowCluesInnerUnmodifiable() {
      assertAll(
          () ->
              assertThrows(
                  UnsupportedOperationException.class,
                  () -> expected.rowClues().getFirst().set(0, 999)),
          () ->
              assertThrows(
                  UnsupportedOperationException.class,
                  () -> expected.rowClues().getFirst().add(999)));
    }

    @Test
    @DisplayName("columnClues() outer list is unmodifiable")
    void columnCluesOuterUnmodifiable() {
      assertAll(
          () ->
              assertThrows(
                  UnsupportedOperationException.class,
                  () -> expected.columnClues().set(0, List.of(1, 2, 3))),
          () ->
              assertThrows(
                  UnsupportedOperationException.class,
                  () -> expected.columnClues().add(List.of(1, 1, 1, 1))));
    }

    @Test
    @DisplayName("columnClues() inner lists are unmodifiable")
    void columnCluesInnerUnmodifiable() {
      assertAll(
          () ->
              assertThrows(
                  UnsupportedOperationException.class,
                  () -> expected.columnClues().getFirst().set(0, 999)),
          () ->
              assertThrows(
                  UnsupportedOperationException.class,
                  () -> expected.columnClues().getFirst().add(999)));
    }
  }

  @Nested
  @DisplayName("Record contract")
  class RecordContract {

    @Test
    @DisplayName("gutters with equal clues are equal")
    void equalCluesAreEqual() {
      Gutter testGutter = new Gutter(rowClues, columnClues);

      assertEquals(expected, testGutter);
    }

    @Test
    @DisplayName("gutters with different row clues are not equal")
    void differentRowCluesNotEqual() {
      rowClues.add(List.of(1, 1, 1, 1));
      Gutter testGutter = new Gutter(rowClues, columnClues);

      assertNotEquals(expected, testGutter);
      assertNotEquals(expected.hashCode(), testGutter.hashCode());
    }

    @Test
    @DisplayName("gutters with different column clues are not equal")
    void differentColumnCluesNotEqual() {
      columnClues.add(List.of(1, 1, 1, 1));
      Gutter testGutter = new Gutter(rowClues, columnClues);

      assertNotEquals(expected, testGutter);
      assertNotEquals(expected.hashCode(), testGutter.hashCode());
    }

    @Test
    @DisplayName("equal gutters have equal hash codes")
    void equalGuttersHaveEqualHashCodes() {
      Gutter testGutter = new Gutter(rowClues, columnClues);

      assertEquals(expected.hashCode(), testGutter.hashCode());
    }

    @Test
    @DisplayName("is not equal to null or to a different type")
    void notEqualToNullOrOtherType() {
      assertNotNull(expected);
      assertNotEquals(Object.class, Gutter.class);
    }

    @Test
    @DisplayName("toString includes both components")
    void toStringIncludesComponents() {
      String expectedRes =
          "Gutter[rowClues=[[1, 2, 1], [1], [2, 2], [2]], columnClues=[[1, 2], [1], [1, 1], [1]]]";
      assertEquals(expectedRes, expected.toString());
    }
  }
}
