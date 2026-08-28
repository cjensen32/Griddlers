# Griddlers Course Standards

These are conventions for this completed tracker, not universal rules imposed by Java.

## Runtime and layout

- Compile production code for Java 21 and use only the Java standard library at runtime.
- Keep JUnit, JaCoCo, Checkstyle, and Spotless as build or test tooling.
- Follow Maven's standard layout: production code in `src/main/java`, tests in `src/test/java`, and generated output in `target`.
- Keep package declarations aligned with paths beneath `com.connorjensen.griddlers`.
- Keep learner-facing curriculum under `lessons/`; it must not be required to build or run the code.

## Dependency direction

- Treat each runnable class in `com.connorjensen.griddlers.tools` as a composition root: it creates concrete dependencies and starts one program.
- Let `com.connorjensen.griddlers.tools` depend on `com.connorjensen.griddlers`, and never the reverse.
- Do not let the engine import `java.io`, `java.nio.file`, `java.net`, or touch `System.out`, `System.err`, or `System.in`.
- Keep the engine deterministic: no clock, no unseeded randomness, no filesystem, no network.
- Inject collaborators through constructors and type fields to interfaces when a useful contract exists.

## Names and visibility

- Use lowercase package names, `UpperCamelCase` types, `lowerCamelCase` methods and variables, and `UPPER_SNAKE_CASE` constants.
- Use explicit ordered imports and no wildcard imports.
- Keep fields private and expose the smallest API callers require.
- In the CLI, only constructors, `ConsoleApplication.run()`, and `TextTable.render(...)` are public.

## Tests

- Follow the [test-writing standards](TESTING_STANDARDS.md), including the three tiers and the maintenance rules.
- Test observable behavior at the narrowest useful boundary.
- Inject streams and other boundaries for deterministic in-process tests; use a subprocess for a real `main`, its exit code, or an EOF lifecycle.
- Restore global streams in `finally` or avoid replacing them.
- Assert exact output for stable contracts and ordered fragments for longer sessions.
- Cover null, empty, whitespace, ragged rows, invalid input, retries, missing values, quit, and EOF where relevant.

## Markdown and documents

- Write one physical line per logical line in every `.md` file and rely on the editor's soft wrap. There is no line-length limit for Markdown, and `max_line_length` is `off` for `*.md` in `.editorconfig`.
- Never break a line mid-sentence, mid-clause, or mid-list-item. A newline inside a paragraph is a claim that the thought ended there, and hard-wrapped prose makes every later edit produce a reflowed diff that hides what actually changed.
- Keep a line break only where it carries meaning: between paragraphs, between the stacked bold-label lines of a document header, between list items, between table rows, and inside fenced code blocks, which are never reflowed.
- Apply the same rule to Markdown embedded in code fences and blockquotes; a blockquote's blank `>` line separates its paragraphs and stays.
- Fix a document's wrapping in its own commit, separate from any change to what the document says, so the prose diff stays readable.

## Quality gates

- Run `mvn verify` for Checkstyle, Spotless, tests, and JaCoCo.
- Treat compiler warnings as review work even when they are not build-blocking.
- Checkstyle reports violations without editing; Spotless rewrites only when `mvn spotless:apply` is run explicitly.
- Run `mvn compile` and the real application after verification because tests alone do not prove the composition root works.

## Boundary

This repository owns the Griddlers engine, its terminal tools, and its browser client. Chapter 1's console tracker is a separate, closed repository; references to it are review material and worked examples, never a build or runtime dependency.
