Two files — read the learning guide first, since it changes how you'd use the spec.

What moved between v0.1 and v0.2:

- **The deduction sequence** (spec §5.3) is the structural change. Java records how the solver reached the answer and ships it with the puzzle, so the web app needs no solver at all. No duplicate implementation, no conformance fixtures.
- **Every deferred optimization now has a trigger** — the condition that would make the simple choice wrong. So "use localStorage" doesn't read as ignorance, it reads as a decision with an expiry.
- **Step 5, the terminal game**, is the checkpoint I'd defend hardest. It proves the engine before any UI exists, so when something's wrong you're debugging one thing.

One thing to flag: steps 7–8 will take longer than the entire solver. Touch input has more edge cases than any algorithm in the project, and that's normal rather than a sign you've done something wrong.

[nonogram-learning-guide.md](./nonogram-learning-guide.md)
[nonogram-spec.md](./nonogram-spec-v2.md)
