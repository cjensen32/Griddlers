package com.connorjensen.griddlers.tools;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public final class TerminalGriddler {
  private final Path destination;

  public TerminalGriddler(Path destination) {
    this.destination = Objects.requireNonNull(destination, "destination is required");
  }

  public void griddlerToFile(String griddlerString) throws IOException {
    Files.writeString(destination, griddlerString, StandardCharsets.UTF_8);
  }
}
