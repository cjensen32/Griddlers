# Agent Backbone

Canonical project authority and agent-only material live under `.agents/`. Learner-facing course material and future learner-owned implementation remain separate.

## Authority map

| File                    | Sole responsibility                                                       |
|-------------------------|---------------------------------------------------------------------------|
| `CONTEXT.md`            | Agent behavior, ownership boundaries, and verification rules              |
| `PROJECT.md`            | Product scope, architecture, definition of done, and non-goals            |
| `COURSE_MAINTENANCE.md` | Chapter-state transitions and course-change ownership                     |
| `reference/`            | Conventions and rationale the agent enforces but the learner rarely opens |
| `skills/`               | Procedures an agent runs, with the tooling each one needs                 |
| `tools/`                | Scripts that check or repair repository documentation                     |
| `archive/`              | Closed material: handoff provenance, Chapter 1, approved plans            |
| `README.md`             | This map, the rules index, and the ownership manifests                    |

Do not duplicate a normative rule across authorities. Keep the rule in its owner and refer to that owner elsewhere.

## Where each rule lives

Change a rule by editing its owner. Nothing here is duplicated, so there is exactly one place to edit for each row.

| To change                                                  | Edit                                | Section                           |
|------------------------------------------------------------|-------------------------------------|-----------------------------------|
| How commit messages are written                            | `CONTEXT.md`                        | Commit messages                   |
| What an agent may never touch                              | `CONTEXT.md`                        | Agent constraints                 |
| Whether an agent may author a lesson                       | `CONTEXT.md`                        | Agent constraints                 |
| What must be verified before a change lands                | `CONTEXT.md`                        | Verification                      |
| What agents may not create before a lesson asks            | `CONTEXT.md`                        | Chapter 2 bootstrap boundary      |
| Markdown wrapping and document shape                       | `reference/COURSE_STANDARDS.md`     | Markdown and documents            |
| Java conventions and the engine boundary                   | `reference/COURSE_STANDARDS.md`     | Dependency direction              |
| Chapter and lesson states, and who moves them              | `COURSE_MAINTENANCE.md`             | Chapter states                    |
| How a submitted lesson is reviewed + what closes the cycle | `skills/review-submission/SKILL.md` | Lane 1, After approval            |
| How a chapter capstone is graded                           | `skills/review-submission/SKILL.md` | Lane 2                            |
| The test tiers and what closes a lesson                    | `lessons/TESTING_STANDARDS.md`      | Test tiers                        |
| What the project is, and is not                            | `PROJECT.md`                        | Product scope, Explicit non-goals |
| Which surface owns a path                                  | `README.md`                         | Ownership manifests               |

## Ownership manifests

### Code and build

Future `src/**`, `pom.xml`, `config/**`, `puzzles/**`, `web/**`, `.editorconfig`, `.gitignore`, `.githooks/**`, and the neutral root `README.md`.

### Learning

`lessons/**` - the syllabus, the test rubric, the journal, and the open chapter's directory. Student-facing only: if the learner would not open it during a lesson, it does not live here.

### Agent backbone

`.agents/README.md`, `.agents/CONTEXT.md`, `.agents/PROJECT.md`, `.agents/COURSE_MAINTENANCE.md`, `.agents/reference/**`, `.agents/skills/**`, `.agents/tools/**`, and `.agents/archive/**`.

`tools/` holds the documentation checks: `unwrap_markdown.py` repairs or reports hard wrapping, and `check_midsentence.py` flags the 'tell' that a file was wrapped. Both take file paths and neither touches anything under `src/`.

`skills/` holds procedures: one directory per skill, each with a `SKILL.md` and whatever tooling that procedure runs. `review-submission/` processes a submitted lesson or a chapter capstone, and its `collect_submission.py` gathers the evidence a review starts from.

`reference/` holds live material the agent enforces: `COURSE_STANDARDS.md`, `LEARNING_GUIDE.md`, and `course-rationale.md`. `archive/` holds closed material: `project-init-session/` for the handoff, `chapter-01/` for the completed chapter's review and glossary, and `plans/` for approved plans once their work has landed.

Every tracked path belongs to one manifest. The root `README.md` may link to every surface but owns no agent rule or lesson contract.

## Dependency direction

`.agents -> lessons -> code` is the allowed documentation direction. `.agents/` may coordinate the other surfaces; lessons may teach against code; code and build must not depend on either documentation surface. Lessons must not require `.agents/` to be understandable.

## Durable and transient state

- Track the four authority documents and the provenance archive.
- Keep transient task notes out of the tracked authority set.
- Treat tool caches and worktrees as local state, never as repository authority.
- Do not delete or regenerate a `.codegraph/` index unless the learner explicitly asks.
- Keep course-documentation changes separate from learner code and progress commits.
