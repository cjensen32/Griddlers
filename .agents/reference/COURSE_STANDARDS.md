# Griddlers Course Standards

These are conventions for this completed tracker, not universal rules imposed by Java.

## Runtime and layout

- Compile production code for Java 21 and use only the Java standard library at runtime.
- Keep JUnit, JaCoCo, Checkstyle, and Spotless as build or test tooling.
- Follow Maven's standard layout: production code in `src/main/java`, tests in `src/test/java`, and generated output in `target`.
- Keep package declarations aligned with paths beneath `com.connorjensen.griddlers`.
- Keep learner-facing curriculum under `lessons/`; it must not be required to build or run the code.

## Dependency direction

- Keep the direction `tools -> engine -> model`, and never the reverse.
- Treat `com.connorjensen.griddlers.Main` as the composition root: it wires concrete dependencies together and starts one program, which is why it is the one place above `tools` that may import from it.
- Treat each runnable class in `com.connorjensen.griddlers.tools` as a composition root of its own, and as the only layer that may reach a file, a socket, or a standard stream.
- Do not let `engine` or `model` import `java.io`, `java.nio.file`, `java.net`, or touch `System.out`, `System.err`, or `System.in`.
- Keep `engine` and `model` deterministic: no clock, no unseeded randomness, no filesystem, no network. An identifier a caller supplies is data; one a constructor mints for itself is unseeded randomness.
- Let `import-control.xml` be the working statement of all of the above. Its comments are part of the rule, and it is expected to change as the code grows rather than to be written once.
- Inject collaborators through constructors and type fields to interfaces when a useful contract exists.

## Names and visibility

- Use lowercase package names, `UpperCamelCase` types, `lowerCamelCase` methods and variables, and `UPPER_SNAKE_CASE` constants.
- Use explicit ordered imports and no wildcard imports.
- Keep fields private and expose the smallest API callers require.
- In the CLI, only constructors, `ConsoleApplication.run()`, and `TextTable.render(...)` are public.

## Tests

- Follow the [test-writing standards](../../lessons/TESTING_STANDARDS.md), including the three tiers and the maintenance rules.
- Test observable behavior at the narrowest useful boundary.
- Inject streams and other boundaries for deterministic in-process tests; use a subprocess for a real `main`, its exit code, or an EOF lifecycle.
- Restore global streams in `finally` or avoid replacing them.
- Assert exact output for stable contracts and ordered fragments for longer sessions.
- Cover null, empty, whitespace, ragged rows, invalid input, retries, missing values, quit, and EOF where relevant.

## Lesson files

- Open every lesson with one line naming a specific, deterministic change on a named file or package - `End goal is to <change> on <target>, so <what it progresses>` - then at most a sentence or two of framing.
- A lesson establishes real features in real files. Completing it must mean the application does something it could not do before, never that a set of instructions was followed.
- Keep the section order: `Reach for`, then the work, then what must be true, then `Close out` at the bottom.
- Make `Reach for` a `Resource` / `What it's for` table of things that can be opened - documentation URLs, package names, class names. Domain resources belong there as much as tooling ones. Label anything on Maven Central `reference, not a dependency`, because runtime stays on the standard library. Confirm every URL is live before shipping the lesson.
- Split a lesson into parts when it exceeds one sitting. Each part ends green, ends with its own commit, and is sized to a comfortable hour. The commit boundary matters as much as the time: one part's work lands together, so the diff between the learner's commits stays readable. A lesson that fits a sitting stays whole.
- Keep work steps short - roughly seven words. Give a step with moving pieces sub-lettered lines rather than a longer sentence, and keep to three to five steps per part. Starting "initialize a class" is a smaller thing than starting "initialize a class, add a file, and delete two fields."
- Mark a step `(optional)` when it is worth doing but is not part of the end product.
- Close every part the same way, as a standing rule stated once rather than a repeated step: tier 1 tests green, `mvn -B verify` green, one commit.
- Write checkboxes as deterministic, observable outcomes - `[ ] print a 4x4 grid with its row and column clues`, never `[ ] understand the boundary`.
- Keep `Close out` questions answerable in a sentence: comprehension first, whose answers are obvious to anyone who did the work, then genuine questions back to the course author about what the next lesson should contain.
- Give every term a lesson introduces a row in `lessons/JOURNAL.md`, so the lexicon stays shared between learner and agent.
- Prefer production code over a fixture. A fixture earns its place only when the observation cannot come from real code, as a rejected import cannot, since a file carrying one fails the build by definition.

## Markdown and documents

- Write one physical line per logical line in every `.md` file and rely on the editor's soft wrap. There is no line-length limit for Markdown, and `max_line_length` is `off` for `*.md` in `.editorconfig`.
- Never break a line mid-sentence, mid-clause, or mid-list-item. A newline inside a paragraph is a claim that the thought ended there, and hard-wrapped prose makes every later edit produce a reflowed diff that hides what actually changed.
- Keep a line break only where it carries meaning: between paragraphs, between the stacked bold-label lines of a document header, between list items, between table rows, and inside fenced code blocks, which are never reflowed.
- Apply the same rule to Markdown embedded in code fences and blockquotes; a blockquote's blank `>` line separates its paragraphs and stays.
- Leave `.agents/archive/**` alone. It is a frozen record of what was said and decided, and reflowing it rewrites history to no benefit.
- Fix a document's wrapping in its own commit, separate from any change to what the document says, so the prose diff stays readable.

## Quality gates

- Run `mvn verify` for Checkstyle, Spotless, tests, and JaCoCo.
- Treat compiler warnings as review work even when they are not build-blocking.
- Checkstyle reports violations without editing; Spotless rewrites only when `mvn spotless:apply` is run explicitly.
- Run `mvn compile` and the real application after verification because tests alone do not prove the composition root works.

## Boundary

This repository owns the Griddlers engine, its terminal tools, and its browser client. Chapter 1's console tracker is a separate, closed repository; references to it are review material and worked examples, never a build or runtime dependency.
