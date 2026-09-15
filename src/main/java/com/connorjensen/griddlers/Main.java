package com.connorjensen.griddlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.connorjensen.griddlers.engine.GriddlerEngine;
import com.connorjensen.griddlers.model.Cell;
import com.connorjensen.griddlers.model.Griddler;
import com.connorjensen.griddlers.model.Gutter;
import com.connorjensen.griddlers.tools.CLIParser;
import com.connorjensen.griddlers.tools.CLIParser.ParsedArgs;
import com.connorjensen.griddlers.tools.TerminalGriddler;

public final class Main {
  private Main() {}

  public static void main(String[] args) throws IOException {
    Path tempDir = Files.createTempDirectory("Griddlers_");

    Path outPath = tempDir.resolve("Griddler.txt");
    int size = 4;
    boolean randomize = false;
    ParsedArgs defaults = new ParsedArgs(outPath, size, randomize);

    ParsedArgs parsed = CLIParser.parse(args, defaults);
    TerminalGriddler terminalGriddler = new TerminalGriddler(parsed.outPath());
    List<List<Cell>> cells;

    if (parsed.randomize()) {
      cells = GriddlerEngine.randomizeCells(parsed.size());
    } else {
      cells = GriddlerEngine.nonRandomizeCells(parsed.size());
    }

    Griddler griddler = new Griddler(cells);
    Gutter griddlerGutters = GriddlerEngine.gutters(griddler);
    String griddlerString = GriddlerEngine.renderWithGutters(griddler, griddlerGutters);

    terminalGriddler.griddlerToFile(griddlerString);
    System.out.print(griddlerString);
  }
}
