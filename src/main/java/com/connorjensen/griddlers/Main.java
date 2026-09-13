package com.connorjensen.griddlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.connorjensen.griddlers.engine.GriddlerEngine;
import com.connorjensen.griddlers.model.Cell;
import com.connorjensen.griddlers.model.Griddler;
import com.connorjensen.griddlers.tools.TerminalGriddler;

public final class Main {
  private Main() {}

  public static void main(String[] args) throws IOException {
    Path outPath = Path.of(System.getProperty("user.home"), "/Downloads/griddler-example.txt");
    TerminalGriddler terminalGriddler = new TerminalGriddler(outPath);

    List<List<Cell>> cells =
        List.of(
            List.of(Cell.FILLED, Cell.FILLED, Cell.FILLED, Cell.FILLED),
            List.of(Cell.UNKNOWN, Cell.UNKNOWN, Cell.UNKNOWN, Cell.UNKNOWN),
            List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY),
            List.of(Cell.EMPTY, Cell.EMPTY, Cell.EMPTY, Cell.EMPTY));
    Griddler griddler = new Griddler(cells);
    String griddlerString = GriddlerEngine.render(griddler);

    terminalGriddler.griddlerToFile(griddlerString);

    System.out.print(Files.readString(outPath, StandardCharsets.UTF_8));
  }
}
