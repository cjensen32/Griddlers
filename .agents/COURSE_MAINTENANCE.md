# Course State and Maintenance

This file governs changes to the course structure. Learner-facing instructions remain under `lessons/` and must not require this file to be understood.

## Ownership

- The learner owns chapter expansion, every implementation file, every test, lesson progress, and decisions recorded by the lesson that makes them.
- Agents may mechanically maintain repository authority, frozen references, provenance, and course prose when the learner requests it.
- Agents may review a learner-drafted chapter plan but may not produce the plan.
- Agents may name missing behavior during a capstone gap analysis but may not write the missing test.

## The four chapter states

1. **Syllabus line:** one sentence stating the chapter's destination.
2. **Manifest:** a goal plus only the guarantees later chapters depend on and which chapter depends on each guarantee.
3. **Expanded:** a learner-drafted lesson breakdown with named verification, written on arrival.
4. **Capstone:** a retrospective gap analysis written after every lesson in the chapter is complete.

Only one chapter may be in state 3. A future `CAPSTONE.md` is a stub containing only the chapter's "Must be true" list until the chapter has been completed.

## Moving a chapter forward

### Syllabus line to manifest

- Work no more than two or three chapters ahead.
- Add only guarantees that a named later chapter would fail without.
- Do not introduce class names, method signatures, parameter order, or implementation choices.

### Manifest to expanded

- The learner drafts the lesson breakdown when reaching the chapter.
- Every lesson names observable verification and its test tier.
- The agent may review for missing prerequisites, ordering problems, or answer leakage.

### Expanded to capstone

- Wait until every lesson and its learner-written tests are complete.
- Grade the code and suite that actually exist instead of prescribing a target in advance.
- Run the demo, verify the suite, perform a code-free gap analysis, and finish with a viva.
- Record the number of Gate 3 gaps in `lessons/JOURNAL.md`.

## Maintenance rules

- Example, never solution. Demonstrate difficult concepts only on unrelated subject matter.
- Preserve the four-state model and the learner's naming and architecture decisions.
- Treat prior chats and the archived handoff as dated evidence, not live authority.
- Make the smallest correction that resolves a demonstrated conflict or stale reference.
- Keep test maintenance explicit: tests may be revised, restructured, or deleted as behavior grows.
- Verify course edits with `git diff --check`, a relative-link audit, an unresolved-variable audit, a scan for implementation or test leakage, and a check that no Markdown line ends mid-sentence.
- Follow the Markdown rule in `lessons/COURSE_STANDARDS.md`: one line per logical line, no hard wrapping, and reflows committed separately from content changes.
