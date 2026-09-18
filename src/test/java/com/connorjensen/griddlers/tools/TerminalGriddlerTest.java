package com.connorjensen.griddlers.tools;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.connorjensen.griddlers.model.Cell;
import com.connorjensen.griddlers.model.Griddler;

class TerminalGriddlerTest {
  private static final List<List<Cell>> CELLS =
      List.of(
          List.of(Cell.EMPTY, Cell.FILLED, Cell.UNKNOWN, Cell.EMPTY),
          List.of(Cell.FILLED, Cell.UNKNOWN, Cell.EMPTY, Cell.FILLED),
          List.of(Cell.UNKNOWN, Cell.EMPTY, Cell.FILLED, Cell.UNKNOWN),
          List.of(Cell.EMPTY, Cell.FILLED, Cell.UNKNOWN, Cell.EMPTY));
  private static final Griddler GRIDDLER = new Griddler(CELLS);
  private static final String EXPECTED_STRING =
      """
      .#x.
      #x.#
      x.#x
      .#x.
      """;

  @Test
  void griddlerOutputsAreEqual(@TempDir Path dir) throws IOException {
    Path target = dir.resolve("griddler.txt");
    Path targetTwo = dir.resolve("griddler2.txt");

    new TerminalGriddler(target).write(EXPECTED_STRING);
    new TerminalGriddler(targetTwo).write(EXPECTED_STRING);

    assertArrayEquals(Files.readAllBytes(target), Files.readAllBytes(targetTwo));
  }

  @Test
  void griddlerToFileProducesIdenticalData(@TempDir Path dir) throws IOException {
    Path target = dir.resolve("griddler.txt");

    new TerminalGriddler(target).write(EXPECTED_STRING);

    assertEquals(EXPECTED_STRING, Files.readString(target, StandardCharsets.UTF_8));
  }
}
