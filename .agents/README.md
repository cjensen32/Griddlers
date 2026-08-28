# Agent Backbone

Canonical project authority and agent-only material live under `.agents/`. Learner-facing course material and future learner-owned implementation remain separate.

## Authority map

| File                            | Sole responsibility                                            |
|---------------------------------|----------------------------------------------------------------|
| `CONTEXT.md`                    | Agent behavior, ownership boundaries, and verification rules   |
| `PROJECT.md`                    | Product scope, architecture, definition of done, and non-goals |
| `COURSE_MAINTENANCE.md`         | Chapter-state transitions and course-change ownership          |
| `archive/project-init-session/` | Handoff provenance, excluding local-only environment files     |
| `README.md`                     | This map and the ownership manifests                           |

Do not duplicate a normative rule across authorities. Keep the rule in its owner and refer to that owner elsewhere.

## Where each rule lives

Change a rule by editing its owner. Nothing here is duplicated, so there is exactly one place to edit for each row.

| To change | Edit | Section |
|---|---|---|
| How commit messages are written | `CONTEXT.md` | Commit messages |
| What an agent may never touch | `CONTEXT.md` | Agent constraints |
| Whether an agent may author a lesson | `CONTEXT.md` | Agent constraints |
| What must be verified before a change lands | `CONTEXT.md` | Verification |
| What agents may not create before a lesson asks | `CONTEXT.md` | Chapter 2 bootstrap boundary |
| Markdown wrapping and document shape | `lessons/COURSE_STANDARDS.md` | Markdown and documents |
| Java conventions and the engine boundary | `lessons/COURSE_STANDARDS.md` | Dependency direction |
| Chapter and lesson states, and who moves them | `COURSE_MAINTENANCE.md` | Chapter states |
| The test tiers and what closes a lesson | `lessons/TESTING_STANDARDS.md` | Test tiers |
| What the project is, and is not | `PROJECT.md` | Product scope, Explicit non-goals |
| Which surface owns a path | `README.md` | Ownership manifests |

## Ownership manifests

### Code and build

Future `src/**`, `pom.xml`, `config/**`, `puzzles/**`, `web/**`, `.editorconfig`, `.gitignore`, `.githooks/**`, and the neutral root `README.md`.

### Learning

`lessons/**`, including the learner's journal and lesson-owned decisions.

### Agent backbone

`.agents/README.md`, `.agents/CONTEXT.md`, `.agents/PROJECT.md`, `.agents/COURSE_MAINTENANCE.md`, and `.agents/archive/**`.

Every tracked path belongs to one manifest. The root `README.md` may link to every surface but owns no agent rule or lesson contract.

## Dependency direction

`.agents -> lessons -> code` is the allowed documentation direction. `.agents/` may coordinate the other surfaces; lessons may teach against code; code and build must not depend on either documentation surface. Lessons must not require `.agents/` to be understandable.

## Durable and transient state

- Track the four authority documents and the provenance archive.
- Keep transient task notes out of the tracked authority set.
- Treat tool caches and worktrees as local state, never as repository authority.
- Do not delete or regenerate a `.codegraph/` index unless the learner explicitly asks.
- Keep course-documentation changes separate from learner code and progress commits.
