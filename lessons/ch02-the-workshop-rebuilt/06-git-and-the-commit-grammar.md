# 2.6 — Git and the commit grammar *(optional)*

*Review — Chapter 1, the commit grammar. Optional: this lesson builds nothing and blocks nothing.*

This was Lesson 2.3 and it is now the last thing in the chapter, because you asked for it to be. You are confident in Git and would rather spend the chapter writing Java, which is the right trade — the machinery this lesson describes is already installed and already working. `C032` did not number itself.

So the lesson changed shape with the slot. It is no longer "build the Git setup". It is a read-through of the conventions this repository already runs on, so that when something eventually looks wrong in `git log` you know which of them broke. Take it when the chapter is otherwise done, or skip it entirely — the capstone's Git guarantee is the one row in the table with nothing technical depending on it.

## What is already true

You do not have to take this lesson's word for any of it. Each of these is one command away.

- `git config --get core.hooksPath` returns `.githooks`, which is why `.githooks/prepare-commit-msg` runs at all. That setting is local to this clone. It is not tracked, it is not cloned, and it is the one thing a person cloning this repository has to do by hand.
- The hook derives the next number by reading `HEAD`'s own subject rather than storing a counter. Diverging branches keep counting from their branch point; nothing drifts, and nothing needs resetting.
- `.gitignore` covers `target`, so `git status` is clean the moment `mvn -B verify` finishes even though the build wrote a jar, a coverage report, Checkstyle's cache, and the Surefire reports.
- `.agents/CONTEXT.md` owns the message grammar: `SCOPE(area): summary`, with `COURSE`, `GAME`, `PROGRESS`, and `FIX` as the four scopes, and a body only where the subject was not enough on its own.
- Commits the course author wrote in full carry `Course Author <noreply@teacher.ai>` as author while you stay the committer, so `git log` shows the same learner-owned and agent-owned split as the rest of the repository. No commit here carries a tool or session trailer.

## Must be true when you're done

Optional, and none of it is new construction.

- [ ] you have read `.githooks/prepare-commit-msg` end to end and can say what each of its three early exits is protecting against
- [ ] you can state, without looking, what a person cloning this repository has to run before their first commit numbers correctly
- [ ] every line in `.gitignore` is one you can justify, and you have checked that nothing Lesson 2.2's plugins produce escapes it
- [ ] one command separates the commits you wrote from the ones the course author wrote

## Done when

- [ ] a commit subject of yours parses as `SCOPE(area): summary` under `.agents/CONTEXT.md`, carrying a body only where the subject was not enough
- [ ] you can say what happens to `C###` numbering when two branches commit in parallel and then merge, and whether it matters

## Reach for

`git log` takes `--author` and a format string. The history since `C001` is worked examples of the grammar.

The hook's own comment block names the enabling command. Notice that this makes the repository self-documenting for a cloner only if they open the file — that gap is recorded in the journal's `Open gaps` table rather than fixed here.

## Pop quiz

Optional, like the rest of the lesson.

1. Hooks are not cloned. What has to happen on a fresh clone before `C###` numbering works, and why is a tracked `.githooks/` directory better here than editing `.git/hooks/` directly?
2. The hook reads the next number off `HEAD` rather than storing a counter. Name something that breaks with a stored counter and does not break here.
3. Two branches diverge, each commits three times, then they merge. What happens to the numbering, and is it actually a problem?
4. What is the difference between a file that is ignored and one that is untracked, and what does `git status` show for each?
5. `.gitignore` lists `target`. Roughly how many files would `git status` offer you after one `mvn verify` if that line were deleted, and why is the count not the point?
6. A single commit carries your test and the course author's edit to a lesson file. Who is the author, who is the committer, and why does this repository keep them apart?
