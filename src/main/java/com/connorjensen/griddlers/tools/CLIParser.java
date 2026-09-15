package com.connorjensen.griddlers.tools;

import java.nio.file.Path;

public class CLIParser {
  private CLIParser() {}

  public record ParsedArgs(Path outPath, int size, boolean randomize) {}

  public static ParsedArgs parse(String[] args, ParsedArgs defaults) {
    Path outPath = defaults.outPath;
    int size = defaults.size;
    boolean randomize = defaults.randomize;

    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case "--out", "-o" -> {
          if (i + 1 >= args.length) {
            throw new IllegalArgumentException(args[i] + " must have a value");
          } else if (args[i + 1].startsWith("-")) {
            throw new IllegalArgumentException("file name cannot start with '-'");
          } else if (!args[i + 1].endsWith(".txt")) {
            throw new IllegalArgumentException("file must end with file extension '.txt'");
          }

          outPath = Path.of(args[++i]);
        }
        case "--size", "-s" -> {
          if (i + 1 >= args.length) {
            throw new IllegalArgumentException(args[i] + " requires an integer");
          } else if (args[i + 1].startsWith("-")) {
            throw new IllegalArgumentException(args[i] + " cannot start with a '-'");
          }
          try {
            size = Integer.parseInt(args[++i]);
          } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + args[i] + "' is not an integer");
          }
          if (size < 1 || size > 50) {
            throw new IllegalArgumentException("size has to be between 1 and 50");
          }
        }
        case "--random", "-r" -> {
          randomize = true;
          if (i < args.length - 1) {
            if (args[i + 1].equalsIgnoreCase("true") || args[i + 1].equalsIgnoreCase("false")) {
              randomize = Boolean.parseBoolean(args[++i]);
            } else if (!args[i + 1].startsWith("-")) {
              throw new IllegalArgumentException(
                  args[i] + " - value has to be true false or blank");
            }
          }
        }
        default -> throw new IllegalArgumentException("Unknown argument at index: " + i);
      }
    }
    return new ParsedArgs(outPath, size, randomize);
  }
}
