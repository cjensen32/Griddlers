# Restructure `lessons/` into a student-facing course surface

## Context

`lessons/` holds 7 documents and ~890 lines of inherited handoff material wrapped around 44 lines of
actual chapter content. The learner has said plainly they will not read it, which makes the whole
surface dead weight.

Two causes, both now removable:

1. `TRANSFER-CHECKLIST.md` § 1 froze those documents as "copy verbatim — frozen reference," and the
   init check audited whether they *matched their source* rather than whether they *belonged there*.
2. `.agents/CONTEXT.md:41` — "Chapter expansion belongs to the learner… an agent may not produce it"
   — meant lesson files could only ever be promoted spec excerpts. That is why `01-the-pom.md` is
   seven lines. It was the rule working, not a style choice.

Outcome: `lessons/` becomes syllabus (include rubric as a table) + journal + the open chapter. Everything else moves
agent-side. Lesson files become real assignments with checkbox rubrics, authored by the agent under
a cycle that refits the queued lesson after each approval.

## Decisions locked

| Decision | Answer |
|---|---|
| Lesson authorship | Agent authors assignments. Learner still writes **every test** — agent may name APIs/concepts to reach for (`assertNull`, `@Nested`), never test bodies |
| **USER ADDED** Lesson authorship - 2 | Agent never touches content inside of `src/` |
| `lessons/` contents | `SYLLABUS.md`, `TESTING_STANDARDS.md` (the rubric), `JOURNAL.md`, chapter directories |
| Migrated docs | Split: Ch1 material → `.agents/archive/`, live conventions → `.agents/reference/` |
| Glyphs | **Unicode** for cell states (Appendix A stands); ASCII `-` `|` `+` for the board frame. Lesson 2.5's UTF-8 rationale survives intact |
| Lesson rubric | Literal filenames, maven path preamble truncated; done-criteria as checkboxes, not command output |
| **USER MODIFIED** Syllabus scope | Goals + checkboxes, chapter/lesson model, Appendix B layout, per-chapter "what this builds" intro + course-rubric=((Capstones + lessons)/assumed-time-to-complete)*100% (only has to include current chapter - plus retain completed ones in this directory). gives overview of what the density will look like and expected percentage of time spent on each chapter. (always equals 100% across all chapters) |
| Commit style | `WHY:` / `CHANGES:` / `VERIFY:`, caps labels, file-specific bullets prefixed with truncated path |

**Assumption to confirm:** **USER CONFIRMED** round 1 question 2 came back "Approved" without a selection. I read that as
the recommended option — `SYLLABUS.md` + `TESTING_STANDARDS.md` + `JOURNAL.md` + chapter dirs.

## Commits

Standing approval given for me to author and commit these. `commit_msg.txt` is gitignored and
currently holds the tracker example — I rewrite it immediately before each commit, not in advance.

| # | Subject | Contents | Whose |
|---|---|---|---|
| C006 | **USER MODIFIED** `COURSE(markdown): unwrap hard-wrapped prose in .md files` | 9 files, already staged | mine |
| C007 | `COURSE(markdown): align table columns` | your padding in `.agents/README.md`, `lessons/CHAPTER-1-REVIEW.md` | yours |
| C008 | `COURSE(unpack): close out the transfer checklist` | your `TRANSFER-CHECKLIST.md` edit | yours |
| C009 |**USER MODIFIED** `COURSE(standards): agent rule changes, one line per thought, lean commit grammar` | Markdown rule, `.editorconfig`, commit-grammar rewrite, rules index | mine |
| C010 | `COURSE(chapters): open one chapter at a time, two lessons deep` | state model in `CONTEXT.md`, `COURSE_MAINTENANCE.md`, `PROJECT.md` | mine |
| C011 | `COURSE(lessons): rebuild lessons as a student-facing surface` | `SYLLABUS.md`, migrations, `ch02/` authored | mine |

**Deviation from "three commits, then restructure":** `ch02/` moves out of C010 into C011 so it is
written once rather than written and immediately rewritten. C010 becomes authority-only.
**USER ADDED NOTE:** commit_msg.txt and commit messages don't always have to contain a `WHY`/`CHANGES`/`VERIFY` block. commit messages can be a heading and that is it. 
  - EX: `C006` current `Subject` should be entire message: Simple, changes are clearly stated, no need for explanation of why. THIS IS THE IDEAL header only COMMIT MESSAGE. Covers the `WHY` and `CHANGES` block (no need for the optional `VERIFY` block)
  - EX: `C009` current `Subject` covers the `CHANGES` content, and most of the `WHY`. Should probably add one line that states further what EXACTLY changed due to it not being clear from the Subject. `{FILE CHANGED} - {AGENT/STUDENT} facing rule needed change to match/update the config in {CONFIG LOCATION}` or `{FILE CHANGED} - Updates made to file for {SPECIFIC AREA, SECTION, OR LINE(S)}`
**USER ADDED NOTE:** commit messages don't need the `VERIFY` block, only really useful when there is interesting/unexpected output (rarely happens) which is important/reasonable enough to qualify (from options below):
  A) Could not be fixed in the commit, adding it for proof or analysis later - rare but may happen when a test/chain or modification happens out of scope. 
  B) Want to record output for quality/progress - EX: 
    """txt
      VERIFY(test) - Stopping here, successful build, 30 tests passed (reference to checkpoint rule)
      ```output.txt
          $ mvn test
          ...
          [INFO] Tests run: 30, Failures: 0, Errors: 0, Skipped: 20
          [INFO] 
          [INFO] ------------------------------------------------------------------------
          [INFO] BUILD SUCCESS
          ...
      ```
  """

## Work

### C009 — standards

- `.agents/CONTEXT.md` § Commit messages: replace `DESCRIPTION:`/`FILES:`/`NOTES:`/`VERIFY:` with
  `WHY:` (one sentence), `CHANGES:` (bullets, file-specific ones prefixed `path/file.md:`, maven
  java paths truncated to `src/test/.../Name.java`), `VERIFY:` (one line). Subject stays
  `SCOPE(area): summary` matching tracker history.
- Markdown rule already drafted in `lessons/COURSE_STANDARDS.md` — moves with that file in C011, so
  C009 places it at its final home in `.agents/reference/COURSE_STANDARDS.md` instead.
- `.editorconfig` `[*.md]` block: already written, unchanged.
- **Rules index** in `.agents/README.md`: a "to change X, edit Y § Z" table so editing agent
  behaviour stops being guesswork. Covers commit format, lesson authorship, test-writing boundary,
  Markdown rule, chapter states, quality gates, engine boundary.

### C010 — chapter and lesson states

Already drafted and in the working tree; carry as-is:

- Chapter states **goal → open → closed**; lesson states **stub → written → green**; exactly two
  lessons written at once.
- `.agents/CONTEXT.md:41` rewrite — agent authors lesson files; `never write test code` stays, gains
  an explicit allowance to name JUnit APIs and concepts.
- Document the A–G cycle in `COURSE_MAINTENANCE.md`, especially **step F**: after a lesson is
  approved, refit the queued lesson to what was actually built, then extend the stub behind it.

### C011 — the restructure

**Moves**

| From | To |
|---|---|
| `lessons/SPEC.md` | `lessons/SYLLABUS.md` (rewritten) |
| `lessons/CHAPTER-1-REVIEW.md`, `lessons/ch01-GLOSSARY.md` | `.agents/archive/chapter-01/` |
| `lessons/LEARNING_GUIDE.md`, `lessons/COURSE_STANDARDS.md` | `.agents/reference/` |
| SPEC Appendices A, C, D, E + the three essay sections | `.agents/reference/course-rationale.md` |

`.agents/README.md` ownership manifest gains `.agents/reference/**`. Every relative link that crossed a moved boundary gets repointed or dropped — `lessons/` must not link into `.agents/`.

**`SYLLABUS.md`** (~90 lines): per-chapter goal line with a "what this builds" opener, milestone checkboxes, the chapter/lesson model, Appendix B's layout, and a closing section naming which file to edit to change how agents write lessons.

**`lessons/ch02-the-workshop-rebuilt/`** — README (**USER NOTE** the length on this file is perfect, ~30 lines, keeps the checkmark style, lists the status of the files, works as a good overview to the chapter) plus six lesson files. 2.1 and 2.2 authored in full, 2.3–2.6 stubs. Lesson shape:

```markdown
# 2.1 — The POM, from memory
*Review — Chapter 1, Lesson 0.*

<2–3 sentences: what this builds and why it comes first>

## Must exist when you're done (**USER MODIFIED SECTION**)
- [ ] `pom.xml` at the repo root (Use ch01's `pom.xml` for reference) 
- [ ] one test under `src/test/.../griddlers/` asserting something trivially true

## Done when (**USER MODIFIED SECTION**)
- [ ] `mvn compile` succeeds
- [ ] `mvn test` reports your test passing `[INFO] Tests run: 1` (fails, errors, skips should be 0)
- [ ] you can say why Surefire is pinned and what breaks when it is not

## Reach for
`@Test` and `assertEquals` from `org.junit.jupiter.api`; `<pluginManagement>` for the pin.

## Pop-quiz (**USER MODIFIED SECTION - Keep it focused on the intricacies and reason we do things a certain way. I offerred the top 4 questions but should be adapted/scoped more correctly depending on the lesson (bottom one is yours) and can have multiple questions, particularly on shorter lessons or lessons that may have breezed by important/useful packages/code/methods. Low level questions are more important, but high level questions about toolchains (like maven or junit) are also important for the java/maven lifecycle**)
What are the steps during the `mvn verify` run, what are they defined by? 
What does running `mvn clean install` do that `mvn install` doesn't?
Why does the entire build fail when running `mvn test` when checkstyle finds a lint error? 
What standard/architecture are we using for the project when it comes to test placements/naming?
Compare afterwards what are the differences in our `pom.xml` and why does it exist?
```

~28 lines each. Source material is the archived draft at
`.agents/archive/project-init-session/10/nonogram-spec-FINAL.md`; no lesson content is invented
beyond turning it into assignment shape.

`CAPSTONE.md` stays a stub holding only the "Must be true" list.

## Verification

Reuse the checks already built this session (scripts in the session scratchpad):

- `unwrap.py --check` over every tracked `.md` — must report no file needing a rewrap.
- `check_midsentence.py` — only the three `**Version:**` header lines may flag.
- Relative-link audit across `README.md`, `.agents/**`, `lessons/**`; additionally assert **no link
  from `lessons/` into `.agents/`**, which would invert the documented dependency direction.
- **USER NOTE** - Keep in mind I love the linking between notes, but when links become broken, the files become stale, or just are unecessary, they are not wanted. Keep them there, just if a file is expected/likely to move/change/modify to the point of link breakage, then don't include it. 
- `git diff --check`; frozen-file comparison against the tracker for `checkstyle.xml` and
  `prepare-commit-msg`; inventory confirming `pom.xml`, `src/`, and package dirs still absent.
- Per-commit: `git show --stat` reviewed before each commit, and `commit_msg.txt` written only at
  that moment.

## Deferred, not forgotten

- (**USER APPROVED/MODIFIED** - Install in the `.agents/tools/` or `.agents/python-scripts/` directory) Installing `unwrap.py` / `check_midsentence.py` as tracked tooling — needs an ownership manifest entry; say where and I will.
- A `/agent-configuration-meeting` skill. The rules index in C009 is the cheaper 80% and ships first. **USER NOTE - Agreed** 
- `.idea/` is untracked and `.gitignore` covers only `.idea/**/workspace.xml`. `.gitignore` belongs
  to Lesson 2.3, so this is yours. **USER NOTE - Agreed**
