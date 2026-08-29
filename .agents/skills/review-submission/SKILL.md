---
name: review-submission
description: Review a submitted Griddlers lesson, or grade a chapter capstone through its four gates. Use when the learner submits or resubmits a lesson, asks to review, approve, close, or grade a lesson or chapter, or asks for a gap analysis or a viva. Also covers what closes the cycle afterwards - the follow-up step, the hours, refitting the queued lesson, and writing the next stub.
---

# Reviewing a submission

This skill owns the procedure for processing a submitted assignment: one lesson, or one chapter's capstone. It is the how. The rules it operates under stay where they live - `.agents/CONTEXT.md` owns what an agent may never do, `.agents/COURSE_MAINTENANCE.md` owns the chapter and lesson states, `lessons/TESTING_STANDARDS.md` owns the tiers, and `.agents/reference/course-rationale.md` owns why the course grades this way. Read `.agents/CONTEXT.md` before you start if this session has not already.

Paths are relative to the repository root. The collector resolves the root itself, so it runs from anywhere in the tree.

## Start here, before opening the submission

Never review from the conversation alone. The learner reports what they think they did; this reports what the repository actually holds.

```sh
python3 .agents/skills/review-submission/collect_submission.py
```

That reads the lesson the chapter README marks `current`. To review a different one, or to grade a chapter, name it:

```sh
python3 .agents/skills/review-submission/collect_submission.py 2.1
python3 .agents/skills/review-submission/collect_submission.py 2.2 --build
python3 .agents/skills/review-submission/collect_submission.py --capstone
```

It prints the chapter's lesson table with each state and its hours, the submitted lesson's checkbox tallies with every unchecked box quoted, which pop-quiz questions have answers under them, the journal's `Definitions` rows for that lesson, `git status` and `git log` and `git diff --check`, the last test results, the test ladder sorted into tiers, and the Markdown checks. `--build` runs `mvn -B verify` first and prints every warning it emitted, or the tail of the output when the build died before it could log one. `--capstone` swaps the single-lesson sections for the chapter's "Must be true" list, the lessons not yet green, and the journal's gap-count table.

The collector reads. It never writes to the repository, and it never opens a file under `src/` for anything but its name.

## The four moves that are always wrong

An index, not a restatement. Each rule lives in one place and this is not it.

| The move you will be tempted to make                                | Where the rule against it lives                      |
|---------------------------------------------------------------------|------------------------------------------------------|
| Editing anything under `src/` to show what you mean                 | `.agents/CONTEXT.md`, Agent constraints              |
| Writing the missing test after you named the gap                    | `.agents/CONTEXT.md`, Agent constraints              |
| Answering "just fix it for me" with the fix                         | `.agents/CONTEXT.md`, Agent constraints              |
| Demonstrating a concept on the class the learner is about to write  | `.agents/CONTEXT.md`, Agent constraints              |

A returned lesson names what is missing and which concept to revisit. It never carries the correction written out.

## Lane 1 - a submitted lesson

1. **Run the collector.** Read its output fully before the lesson file.
2. **Check the boxes against reality.** A checked box whose evidence is not in the collector's output is the first thing to ask about. `mvn verify` green does not close a box that names something else.
3. **Read the pop-quiz answers for mechanism, not for vocabulary.** An answer can name every part correctly and still describe the wrong causal chain. Stated confidence percentages are context and never a routing signal - a confidently wrong answer is the case the follow-up step exists for.
4. **Audit the journal's `Definitions` rows for this lesson without being asked to.** Flag a wrong scope or a wrong definition. The rows track the quiz: in Lesson 2.1 the two wrong rows, `packaging` and `dependency scope`, were the same two gaps as the weakest answers.
5. **Offer the journal line.** When any part of the review amounts to "this broke, and this fixed it", offer the one-line `lessons/JOURNAL.md` entry before moving on. The learner should not have to remember the journal exists; prompting is the reviewer's job.
6. **Approve, or return it.** Return it with what is missing and which concept to revisit.
7. **Offer the follow-up step.** See below.
8. **Close the cycle.** See "After approval".

### The follow-up step

Close the review by offering one optional extra step, written into the lesson file as a checkbox tagged `(mastery)`, `(reinforce)`, or `(clarify)`.

Put it where the thing it targets already lives - a `Done when` box when the build can settle it, a sub-bullet under the pop-quiz question it re-opens - never under a heading of its own. It matches the register of what surrounds it: one observable action, one line, about as long as the boxes above it. A step needing a paragraph to explain itself has not been reduced to its observation yet, and a checkbox phrased as a question is a quiz item wearing a checkbox.

- `(mastery)` - the lesson passed and the quiz held up. One task combining two things the lesson covered, harder than either alone.
- `(reinforce)` - it passed, but an answer was weak. Re-derive the weakest one from an observation the learner can make themselves.
- `(clarify)` - it did not pass. Name the observation and the concept to revisit, never the file and never the fix.

Judge "weak" from the review rather than from the stated confidence percentage. A `(clarify)` step that says "check that Surefire is in `build.plugins`" is the forbidden correction with a checkbox around it - the step names what to observe and lets the observation do the teaching.

The learner accepts or declines. A declined step does not block green: `.agents/COURSE_MAINTENANCE.md` defines green as the named test passing and the commit landing, and nothing here overrides that.

The rule as written fires on a first submission only. Lesson 2.1 resubmitted with revised answers and the `(reinforce)` branch fired on the second pass instead - see `.agents/archive/plans/chapter02-lesson01-2026-08-28-journal-definitions.md`. That edge is open and has not been decided. Say which pass you are treating as first rather than deciding it silently.

A step is offered once. On the pass after it was offered, judge what it produced and retire the heading either way - reissuing an unfinished step keeps a green lesson open over work a later lesson already covers. Lesson 2.1 closed on its third pass with one of the two landed, the other carried into 2.2 and 2.4 where the same mechanism is load-bearing.

## Lane 2 - a chapter capstone

Run `--capstone` first. Every lesson must be green before `CAPSTONE.md` is written past its "Must be true" list, and the collector says which are not.

Four gates, in order, stopping at the first failure. The capstone grades what exists rather than prescribing what should have been built.

**Gate 1 - demo.** The learner runs it and pastes the output. It does the thing the chapter set out to do, and you can see it.

**Gate 2 - the suite runs.** `mvn verify` green. Every "Must be true" entry has at least one test that would fail if the guarantee stopped holding, and the learner points at which test covers which entry. An entry they cannot point at is untested, whatever the coverage report says.

**Gate 3 - gap analysis.** Read the production code and the tests and report, without writing any code: behaviour the suite does not exercise, named specifically enough to be actionable; tests at the wrong tier; tests to delete as trivial, duplicated, outdated, or coverage-chasing; tests coupled to implementation detail that will break on the next refactor; and shape observations on the production code, offered as questions rather than corrections. Count the gaps and tell the learner the number so they can record it.

**Gate 4 - viva.** Draft the questions from what was actually built, ask them one at a time, and judge the answers. Editor closed.

### The prompt the learner pastes to start it

> Grade my work for Chapter N of my Nonogram course. Four gates, in order, stop at the first failure.
>
> **Gate 1:** My terminal output — [paste]. Here's what the chapter set out to do — [paste goal].
>
> **Gate 2:** My `mvn verify` output and my full test file list — [paste]. Here is the chapter's "Must be true" list — [paste]. For each entry, I claim this test covers it: [list]. Tell me where I'm wrong.
>
> **Gate 3:** Read my production code and my tests and give me a gap analysis: behaviour I haven't covered, tests at the wrong tier, tests I should delete, and tests that are coupled to my implementation. Name the gaps specifically. **Do not write any test code.** Tell me how many gaps you found so I can record it.
>
> **Gate 4:** Draft viva questions from what I actually built, ask them one at a time, and judge my answers.
>
> **Rules for you:** Do not write, rewrite, or show me any implementation or test code at any point. If you need to show me what a concept looks like, use an example on unrelated code — never on mine. If I fail a gate, tell me *what* is wrong and *which concept* to revisit, not how to fix it. If I ask for the fix, refuse and restate the concept.

The learner does not have to paste it. The gates run the same way when they simply ask for a grade.

### Closing the chapter

The learner records the Gate 3 count in the `Capstone gap counts` table in `lessons/JOURNAL.md`, and the chapter's hours roll up.

```sh
python3 .agents/tools/roll_up_hours.py --check lessons/ch02-the-workshop-rebuilt/README.md
```

Drop `--check` to write the README's `Total` row and the syllabus course map in one step. Blank and em-dash cells count as zero, so a missing lesson's hours silently lower the total - the collector flags any green lesson with an empty Hours cell for exactly this reason.

## After approval - the maintenance cycle

The lesson is not closed when the review ends. Seven steps, in order. Narrate each one in chat for the first few cycles of a chapter; the learner is calibrating against the process itself, not only against the lesson.

1. **Process the learner's feedback first, and change nothing while doing it.** This step is consensus, not edits. Feedback arriving with a submission is the highest-value moment in the cycle - it is when the course can still be reshaped cheaply. Push back where the feedback would cost more than it earns, say so plainly, and put genuine forks to the learner as multiple choice rather than guessing. Nothing is written until the shape is agreed. Every policy edit the consensus produces lands at step 6, not here.
2. **The learner commits their own work.** Not you, and never without their approval. `.agents/CONTEXT.md` owns the message grammar and the authorship split. Ask roughly how many hours the lesson took and record it in the chapter README's Hours column - a guess is fine, an unrecorded lesson is not.
3. **Refit the queued lesson to what was actually built.** This is the step that keeps the chapter coherent. The queued lesson was written before the learner's choices existed, so its assumptions and its verification get corrected against the approved result before the learner ever opens it. Refit against what the submitted code actually contains rather than what its lesson asked for - the gap between those two is the whole reason this step exists.
4. **Review the journal, and record what the lesson left open.** Mark rows still wrong with a `?n` footnote; add the next lesson's terms with definitions left blank; write every carried gap into the `Open gaps` table with where it settles. The chapter README's `Open` count is that table's row count for the lesson, so a number that does not match its rows is a bug in one of the two.
5. **Write out the stub behind the refitted lesson,** but only after the learner confirms the refitted lesson is available and they have opened it. Exactly two lessons are written at a time; the collector flags it when a third appears.
6. **Propagate upstream.** Hours roll into the README total and the syllabus course map together via `.agents/tools/roll_up_hours.py`. Policy agreed at step 1 lands here, in `.agents/`, in one place rather than scattered across the cycle.
7. **Write `commit_msg.txt` and stop at every commit.** One message per commit, naming the exact files that commit carries, because the learner's working tree may hold unrelated work. The learner has final say on every message before it lands. Commits group by surface, with a lesson refit standalone so its diff can be read on its own. Once they have all landed, write the session artifact under `.agents/archive/session-artifacts/` as a log of the cycle: each step, and what changed at it.

## Verifying your own edits

A review usually ends with an edit to the lesson file - the follow-up checkbox, a refit, a new stub. That is a documentation change and `.agents/CONTEXT.md` requires it verified. The collector runs the checks over the files it already knows about; for the whole repository:

```sh
find . -name '*.md' -not -path './.agents/archive/*' -not -path './target/*' -print0 | sort -z | xargs -0 python3 .agents/tools/unwrap_markdown.py --check
find . -name '*.md' -not -path './.agents/archive/*' -not -path './target/*' -print0 | sort -z | xargs -0 python3 .agents/tools/check_midsentence.py
```

Known false positives, all pre-existing: `rewrapped` on `lessons/JOURNAL.md` and on any lesson file holding answers, and `ends mid-sentence` on the `**Version:**` header lines of `lessons/SYLLABUS.md` and `.agents/reference/LEARNING_GUIDE.md`. Anything else is yours.

## Gotchas

- **A green build is not a correct POM.** When this skill was written, `mvn -B verify` reported `BUILD SUCCESS` on this repository while warning that the `<scope>default</scope>` left behind by Lesson 2.1's scope experiment is not a scope Maven recognises. The collector prints every warning under `--build` for this reason. Gate 2 and any "Done when" box that says `mvn verify` passes are both satisfied by a build that is quietly wrong.
- **Surefire reports outlive the code that produced them.** `target/surefire-reports/` holds the last run, not the current one. The collector compares its timestamps against the newest file under `src/` and says `reports are older than the newest file under src/` when they disagree. Rerun with `--build` rather than trusting the number.
- **Never run `unwrap_markdown.py` in rewrite mode over a file holding learner answers.** `--check` only, always. It reports `rewrapped` on both `lessons/JOURNAL.md` and `lessons/ch02-the-workshop-rebuilt/01-the-pom.md`, and both are false positives covering real damage it would do: the journal's `!1`-style footnotes are prose lines it joins into one paragraph, and a pop-quiz answer split into `A.` and `B.` sub-lines gets folded up into the question, because `A.` is not a list marker the tool recognises. `.agents/CONTEXT.md` tells you to run this over every Markdown file outside the archive; run it with `--check`.
- **`git diff --check` fires on the learner's own answers.** Trailing whitespace inside a pop-quiz answer is theirs. Name it if it matters; do not clean it up.
- **The chapter README table is the state machine.** `State` drives everything the collector reports, so a lesson left at `current` after it went green makes the next review target the wrong file.
- **Maven needs a JVM the shell can find.** `mvn` comes off SDKMAN's `current` symlink and resolves fine on its own, so a shell with a trimmed environment reports `BUILD FAILURE` with `Unable to locate a Java Runtime` - which is not a Maven error and says nothing about the submission. `--build` prints the tail of the output when the build dies before it can log, so read what it actually says before believing the build is broken.
- **The confidence percentages are self-reported and often inverted.** Lesson 2.1's Q4 was stated at 85% and is roughly 20% right. Keyed on the number, the worst answer on the sheet routes to `(mastery)`.

## Troubleshooting

- `No lesson file for None. Pass a number from the table above.` - no row in the chapter README is marked `current`. Pass the lesson number explicitly, then fix the table.
- `No lesson file for '9.9'.` - the number is not in the table. The collector prints the table above the error; use a number from it.
- Collector shows `?? .agents/skills/` under Git - the skill directory is untracked until the learner commits it. Expected on a fresh checkout of this work.
- `FileNotFoundError` naming a path with newlines in it, when checking many files at once - the shell here is zsh, which does not word-split an unquoted variable. Use the `find -print0 | xargs -0` form above rather than `--check $files`.
- A traceback out of the collector - it is agent tooling and may be edited, unlike everything under `src/`. Fix it and say so.
- `! more than two lessons are written at once` - a lesson was written ahead. That is the failure the two-at-a-time model exists to prevent; do not paper over it by refitting the extra one.
