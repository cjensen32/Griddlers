# Course State and Maintenance

This file governs changes to the course structure. Learner-facing instructions remain under `lessons/` and must not require this file to be understood.

## Ownership

- The learner owns everything under `src/`, every test, lesson progress, and the decisions recorded by the lesson that makes them.
- Agents write the open chapter's lesson files and keep them fitted to what the learner has actually built.
- The learner may rewrite any lesson file. An assignment is a proposal about how to spend the next session, not a contract.
- Agents may name missing behavior during a capstone gap analysis but may not write the missing test.

## Chapter states

1. **Goal:** one sentence stating the chapter's destination, and nothing else. Every chapter carries this from day one and keeps it until it opens.
2. **Open:** the chapter owns a directory under `lessons/` holding its README, its lesson files, and a stub `CAPSTONE.md`.
3. **Closed:** every lesson is green and the capstone has been written from the code and tests that exist.

Only one chapter is open at a time. A chapter that is not open has no lessons, no guarantees, and no test plan anywhere in the repository. Do not draft them, and do not restore them from the archive or from Git history into a live document.

## Lesson states inside the open chapter

1. **Stub:** a title and an empty verification line. Every lesson starts here when the chapter opens.
2. **Written:** the lesson's task and its named verification with test tier.
3. **Green:** the named test passes and the commit has landed.

Exactly two lessons are written at a time — the current one and the one queued behind it. When the current lesson goes green, the next stub is written out and a new stub joins the queue. Writing further ahead is the failure this model exists to prevent: a lesson drafted five ahead assumes the earlier ones were solved its way, and when they were not, either the earlier lesson gets padded to fit or the later one stops making sense.

Keep lesson files short. A lesson states what to build and what to verify; the explanation belongs in the conversation, and the proof belongs in the test.

## Moving a chapter forward

### Goal to open

- The learner opens the chapter on arrival, never earlier.
- Create the directory, the stub per lesson, and the stub `CAPSTONE.md`.
- The chapter's "Must be true" list is authored at this point and lives only in that stub.

### The lesson cycle

One turn of this loop per lesson. The agent writes the assignment; the learner writes every line of code and every test in it.

1. The agent writes the lesson file: what it builds, what must exist when it is done, what closes it, and which tools to reach for.
2. The learner works on it.
3. The learner asks questions against the lesson, which the agent answers with concepts and examples on unrelated code. When an answer amounts to "this broke, and this fixed it", offer the one-line `lessons/JOURNAL.md` entry for it before moving on. The learner should not have to remember the journal exists; prompting is the agent's job. When the learner submits pop-quiz answers for review, read the filled rows in the journal's `Definitions` table as well and flag a wrong scope or definition; the table is checked without being asked about.
4. The learner submits the lesson.
5. The agent approves it, or returns it with what is missing and which concept to revisit - never with the correction written out.
   - On a first submission only, close the review by offering one optional extra step, written into the lesson file as a checkbox tagged `(mastery)`, `(reinforce)`, or `(clarify)`. Offer `(mastery)` when the lesson passed and the quiz held up: one task combining two things the lesson covered, harder than either alone. Offer `(reinforce)` when it passed but an answer was weak: re-derive the weakest one. Offer `(clarify)` when it did not pass: name the observation and the concept to revisit, never the file and never the fix. Judge "weak" from the review rather than from the stated confidence percentage, because a confidently wrong answer is the case this exists for. The learner accepts or declines, and a declined step does not block green.
6. The learner commits. Ask roughly how many hours the lesson took and record it in the chapter README's per-lesson Hours column - a guess is fine, an unrecorded lesson is not.
7. **The agent refits the queued lesson to what was actually built.** This is the step that keeps the chapter coherent: the queued lesson was written before the learner's choices existed, so its file names, its assumptions, and its verification are corrected against the approved result before the learner ever opens it.
8. Only once the queued lesson is refitted does the agent write out the stub behind it, so a written lesson is always waiting.

Every lesson names observable verification and its test tier. The learner writes the test; naming a gap is help, filling it is not.

### Open to closed

- Wait until every lesson and its learner-written tests are complete.
- Grade the code and suite that actually exist instead of prescribing a target in advance.
- Run the demo, verify the suite, perform a code-free gap analysis, and finish with a viva.
- Record the number of Gate 3 gaps in `lessons/JOURNAL.md`.
- Run `.agents/tools/roll_up_hours.py` over the chapter README so the per-lesson hours sum into its Total row and the syllabus course map in one step.

## Maintenance rules

- Example, never solution. Demonstrate difficult concepts only on unrelated subject matter.
- Preserve the four-state model and the learner's naming and architecture decisions.
- Treat prior chats and the archived handoff as dated evidence, not live authority.
- Make the smallest correction that resolves a demonstrated conflict or stale reference.
- Keep test maintenance explicit: tests may be revised, restructured, or deleted as behavior grows.
- Verify course edits with `git diff --check`, a relative-link audit, an unresolved-variable audit, a scan for implementation or test leakage, and a check that no Markdown line ends mid-sentence.
- Follow the Markdown rule in `.agents/reference/COURSE_STANDARDS.md`: one line per logical line, no hard wrapping, and reflows committed separately from content changes.
