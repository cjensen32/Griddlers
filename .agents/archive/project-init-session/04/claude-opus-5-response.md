Restructured into 13 chapters (0–12), each with lessons, a verification test per lesson, an API contract, and a four-gate capstone.

Three things worth calling out:

**The renderer moved to Chapter 1, before clues or any solving.** Everything after it is debugged by looking at its output. That forced a supporting rule into the standing rules section: rendering functions return Strings and only `main` prints. Without it you can't assert on a board character-for-character, and half the tests in the spec become impossible.

**Gate 3 is the part that actually tests validity.** The agent writes fresh tests against the chapter's contract — signatures only — and you read only the failure names, never the test bodies. Gate 2 just proves your code passes tests you thought of, which is exactly the blind spot you want found. There's a paste-ready prompt in the "How this course works" section that runs all four gates and instructs the agent to refuse to give you fixes.

**The contracts have to stay stable** once you start a chapter. If you rename `render` to `toString` halfway through, the agent's hidden tests won't compile and Gate 3 silently stops working.

One structural note: Chapters 8–11 are web work, so JUnit drops out. Gate 3 becomes an agent-written adversarial QA checklist instead of generated tests. It's a weaker gate, honestly — worth knowing that the rigor thins out exactly where the edge cases multiply.
[nonogram-spec.md](./nonogram-spec-v3.md)
[nonogram-learning-guide.md](./nonogram-learning-guide-v2.md)
