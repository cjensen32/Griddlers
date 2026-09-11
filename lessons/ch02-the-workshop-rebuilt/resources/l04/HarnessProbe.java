package com.connorjensen.griddlers;

// Harness fixture for Lesson 2.4. One argument, one behaviour, no imports - so it makes
// no decision for you about where your harness is allowed to live.
// Copy to src/test/java/com/connorjensen/griddlers/, drive it from your harness, delete.
// Not named *Test, so Surefire leaves it alone.

public final class HarnessProbe {
  private HarnessProbe() {}

  public static void main(String[] args) throws Exception {
    String what = args.length > 0 ? args[0] : "";
    switch (what) {
      case "silent" -> {
        // exits 0 having written nothing
      }
      case "line" -> System.out.println("one line");
      case "small" -> System.out.print("x".repeat(1024));
      case "flood" -> System.out.print("x".repeat(200_000));
      case "floode" -> System.err.print("x".repeat(200_000));
      case "glyphs" -> System.out.println("█·✕▓");
      case "echo" -> {
        byte[] in = System.in.readAllBytes();
        System.out.write(in);
        System.out.flush();
      }
      case "hang" -> {
        while (true) {
          Thread.sleep(60_000L);
        }
      }
      default -> System.exit(3);
    }
  }
}
