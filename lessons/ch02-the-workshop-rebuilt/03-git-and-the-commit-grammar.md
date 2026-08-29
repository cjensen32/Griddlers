# 2.3 — Git and the commit grammar

*Review — Chapter 1, the commit grammar.*

Git has been doing two things for you that you never configured. Something numbers every commit `C###`. Something keeps `target/` out of `git status` even though `mvn verify` fills it on every run. Both arrived from Chapter 1 as frozen reference, and neither is magic. This lesson is where you take them apart and take ownership of the message grammar they exist to support.

## Must be true when you're done

- [ ] a fresh clone of this repository numbers its first commit correctly, following only what the repository itself tells the person cloning it
- [ ] `git status` is clean the moment `mvn -B verify` finishes
- [ ] no line in `.gitignore` is one you cannot justify, and nothing Lesson 2.2's plugins produce escapes it
- [ ] one command separates the commits you wrote from the ones the course author wrote

## Done when

- [ ] a commit subject of yours parses as `SCOPE(area): summary` under `.agents/CONTEXT.md`, carrying a body only where the subject was not enough
- [ ] you can say what happens to `C###` numbering when two branches commit in parallel and then merge
- [ ] `git status` is clean and the commit has landed

## Reach for

`.githooks/prepare-commit-msg` is already in the repository and derives its next number by reading `HEAD`'s own subject. Hooks are not cloned with a repository; something has to point Git at that directory, and that something is a local setting rather than a tracked file.

`.agents/CONTEXT.md` owns the grammar — the four scopes, when a body earns its place, and why a commit the course author wrote carries a different author from one you wrote. The history since `C001` is worked examples of it.

`git log` takes `--author` and a format string.

## Pop quiz

1. Hooks are not cloned. What has to happen on a fresh clone before `C###` numbering works, and why is a tracked `.githooks/` directory better here than editing `.git/hooks/` directly?
2. The hook reads the next number off `HEAD` rather than storing a counter. Name something that breaks with a stored counter and does not break here.
3. Two branches diverge, each commits three times, then they merge. What happens to the numbering, and is it actually a problem?
4. What is the difference between a file that is ignored and one that is untracked, and what does `git status` show for each?
5. `.gitignore` lists `target`. Roughly how many files would `git status` offer you after one `mvn verify` if that line were deleted, and why is the count not the point?
6. A single commit carries your test and the course author's edit to a lesson file. Who is the author, who is the committer, and why does this repository keep them apart?
