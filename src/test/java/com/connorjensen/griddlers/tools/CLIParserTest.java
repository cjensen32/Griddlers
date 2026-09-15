package com.connorjensen.griddlers.tools;

import static com.connorjensen.griddlers.tools.CLIParser.ParsedArgs;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class CLIParserTest {

  @Nested
  @DisplayName("ParsedArgs record tests")
  class ParsedArgsTests {
    @ParameterizedTest
    @DisplayName("Constructor preserves values")
    @CsvSource({"testFile.txt, 20, true", "example.txt, 5, false", "test.txt, 100, true"})
    void constructorPreservesValues(
        String fileName, int size, boolean randomize, @TempDir Path dir) {
      Path outDir = dir.resolve(fileName);
      ParsedArgs parsedArgs = new ParsedArgs(outDir, size, randomize);

      assertAll(
          () -> assertEquals(outDir, parsedArgs.outPath()),
          () -> assertEquals(size, parsedArgs.size()),
          () -> assertEquals(randomize, parsedArgs.randomize()));
    }
  }

  @Nested
  @DisplayName("Parse method/function tests")
  class ParseTests {
    private static ParsedArgs DEFAULTS;
    private static final int SIZE = 10;
    private static final boolean RANDOMIZE = false;
    @TempDir private static Path DIR;

    @BeforeEach
    void setUp() {
      DEFAULTS = new ParsedArgs(DIR.resolve("griddler.txt"), SIZE, RANDOMIZE);
    }

    @ParameterizedTest(name = "{0} ==> throws {1}")
    @CsvSource({
      "--out -s 20 --random, file name cannot start with '-'",
      "--out -r, file name cannot start with '-'",
      "-o , -o must have a value",
      "--out , --out must have a value",
      "-o error.pdf, file must end with file extension '.txt'", /* End of output/file flag tests */
      "-s, -s requires an integer",
      "-s 500, size has to be between 1 and 50",
      "-s -100, -s cannot start with a '-'",
      "--size 5s, '5s' is not an integer", /* End of size flag tests*/
      "--random facts, --random - value has to be true false or"
          + " blank", /* End of random flag tests */
      "-R -s 20 --out test.txt, Unknown argument at index: 0",
      "-r -S 20 --out test.txt, Unknown argument at index: 1",
      "-r -s 20 --outDir test.txt, Unknown argument at index: 3",
      "-r -s 20 --out test.txt --fake, Unknown argument at index: 5" /* End of default flag tests */
    })
    void flagsThrowOnInvalidValues(String argsString, String errorText) {
      String[] args = argsString.split(" ");

      IllegalArgumentException exception =
          assertThrows(IllegalArgumentException.class, () -> CLIParser.parse(args, DEFAULTS));

      assertEquals(errorText, exception.getMessage());
    }

    @ParameterizedTest(name = "{0} converts correctly to output")
    @CsvSource({
      ", griddler.txt, 10, false", /* no args, default used */
      "-s 20, griddler.txt, 20, false",
      "--size 20, griddler.txt, 20, false",
      "-o test.txt, test.txt, 10, false",
      "--out false.txt, false.txt, 10, false",
      "--random, griddler.txt, 10, true",
      "--random false, griddler.txt, 10, false",
      "-r true, griddler.txt, 10, true",
      "-r false, griddler.txt, 10, false"
    })
    void argumentsWorkCorrectly(String argsString, String path, int size, boolean randomize) {
      String[] args;
      if (argsString != null) {
        args = argsString.split(" ");
      } else {
        args = new String[0];
      }

      resolveDirFromArgs(args);

      ParsedArgs expected = new ParsedArgs(DIR.resolve(path), size, randomize);
      assertEquals(expected, CLIParser.parse(args, DEFAULTS));
    }

    private void resolveDirFromArgs(String[] args) {
      for (int i = 0; i < args.length; i++) {
        if (args[i].endsWith("txt")) {
          args[i] = DIR.resolve(args[i]).toString();
          break;
        }
      }
    }
  }
}
