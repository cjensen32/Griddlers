# ${PROJECT_NAME} — Course Handoff

Everything needed to start Chapters 2–14 of this course in a new, empty repository. Chapter 1 is
complete and closed in a separate repository; this directory is the bridge.

**Nothing here is code.** No `pom.xml`, no Java, no tests. Those are Chapter 2's lessons and belong
to the learner. What this directory carries is the course design, the conventions, the names, and an
exact list of what to move.

Read this before the move. Afterwards this directory becomes provenance in `${SPEC_HISTORY_DIR}/`,
the three live documents are promoted into `${LESSONS_DIR}/` under new names, and the file references
below describe where things *were*, not where they are.

---

## What's here

| File | What it is | Read when |
|---|---|---|
| `README.md` | This map | First |
| `project.env` | Single source of truth for every name, path, link, and locked choice | First — nothing else resolves without it |
| `TRANSFER-CHECKLIST.md` | Exactly what to move, with every change written out inline | Doing the move |
| `nonogram-spec-FINAL.md` | The course: chapter model, test ladder, capstone protocol, Chapters 2–14 | Before starting Chapter 2 |
| `nonogram-learning-guide-FINAL.md` | How to work: the rules, how to use an assistant, traps | Before the spec |
| `CHAPTER-1-REVIEW-FINAL.md` | Recall drills for what Chapter 1 already taught | Before Chapter 2 |
| `HANDOFF-FINAL.md` | Why the course is shaped this way; the design decisions and their reasons | When something here seems arbitrary |
| `01/` … `09/` | Chat history that produced the above. Provenance, not instructions | Never, unless auditing a decision |

Documents reference each other by filename and reference everything else by `project.env` variable.
There are no dangling references; the checklist's section 6 has the command that proves it.

---

## Read order

1. `project.env` — skim it. It tells you what this project is called and where everything lives.
2. `nonogram-learning-guide-FINAL.md` — how to work. Short.
3. `nonogram-spec-FINAL.md` — the course itself. Sections "Why this course is structured
   differently from Chapter 1" and "How a chapter works" are the load-bearing ones.
4. `CHAPTER-1-REVIEW-FINAL.md` — answer its drills before Chapter 2 asks you to rebuild anything.
5. `TRANSFER-CHECKLIST.md` — then do the move.

---

## Two ways to do the move

The checklist tags every task **Agent** or **Learner**. The split is not about difficulty; it is
about which tasks are lessons.

### Manual

Work down `TRANSFER-CHECKLIST.md` and tick boxes. Every change is written out inline — paste-ready
blocks for new content, unified diffs for edits. Nothing requires interpretation.

### Agent-assisted

An agent may do the mechanical unpack: copying files, applying the written-out edits, moving this
directory into place, and creating the `.agents/` governance documents. It may not create anything
Chapter 2 teaches.

Paste this, from inside the new empty repository, with this directory already copied in:

```text
You are unpacking a course handoff into this repository. Read these four files completely
before doing anything: README.md, project.env, TRANSFER-CHECKLIST.md, and HANDOFF-FINAL.md
(in the handoff directory).

Then perform ONLY the tasks tagged **Agent** in TRANSFER-CHECKLIST.md, in the order given.

Start with .agents/CONTEXT.md. Until that file exists, the constraints below live only in
this prompt — writing it is what makes them durable for every agent after you.

You must NOT, for any reason, even if asked:
  - write or scaffold any Java file, any test file, or pom.xml
  - create or edit .gitignore, or add any module to checkstyle.xml
  - create src/main or src/test trees, or any package directory
  - run git init, git add, or git commit
  - fill in any CAPSTONE.md beyond the "Must be true" list already in the spec
  - create CLAUDE.md, AGENTS.md, .claude/, or any provider-specific file as authority

Every one of those is a Chapter 2 lesson and belongs to the learner. If you believe one is
required, say so and stop rather than doing it.

When done, report: every file you created or modified, every checklist box you could not
tick and why, and every project.env entry still marked # REVIEW.
```

The constraint that matters most is the first prohibition. This course exists because Chapter 1
handed over a 39-test grader and a published contract, and the learner ended up shaping code to fit
someone else's box. An agent that helpfully scaffolds a `pom.xml` reproduces that exact failure on
day one.

---

## Where this lands

```
${PROJECT_LOCAL_REPO}/
├─ project.env                      moved to the root — live config, not history
├─ ${JOURNAL_FILE}                       created fresh
├─ README.md                        created fresh
├─ .agents/                         created fresh — CONTEXT.md first
├─ ${CHECKSTYLE_CONFIG}   copied
├─ ${COMMIT_HOOK}       copied
├─ .editorconfig                    copied
├─ ${LESSONS_DIR}/
│  ├─ SPEC.md                       promoted from nonogram-spec-FINAL.md
│  ├─ LEARNING_GUIDE.md             promoted from nonogram-learning-guide-FINAL.md
│  ├─ CHAPTER-1-REVIEW.md           promoted from CHAPTER-1-REVIEW-FINAL.md
│  ├─ COURSE_STANDARDS.md           copied, then six lines edited
│  ├─ TESTING_STANDARDS.md          copied, two sections appended, one replaced
│  └─ ch01-GLOSSARY.md              copied verbatim, never edited again
└─ ${SPEC_HISTORY_DIR}/             the rest of this directory, read-only
```

Absent on purpose, because Chapter 2 builds them: `pom.xml`, `.gitignore`, `src/main`, `src/test`.

---

## After the move

`${LESSONS_DIR}/SPEC.md`, Chapter 2, Lesson 2.1. Write the POM blind, then diff it against Chapter
1's and account for every difference.
