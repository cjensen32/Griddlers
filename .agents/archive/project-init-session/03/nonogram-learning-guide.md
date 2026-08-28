# Nonogram — Learning Guide

**Companion to:** `nonogram-spec.md`
**Purpose:** how to work on this project so that you end up knowing how to build it.

---

## 1. The rule

**Every line of code in this project gets typed by you, from your own understanding.**

Not "typed by you after reading a generated version." The distinction is the whole project. Reading working code produces a warm feeling of comprehension that evaporates the moment you face a blank file — and you won't notice the gap until you're four milestones deep and can't debug your own program.

If you break the rule once for a genuinely stuck moment, fine. If you break it habitually, you'll have an app and no skills, which is the outcome you're specifically trying to avoid.

---

## 2. How to use an AI assistant here

Assistants are enormously useful on this project. Just not for writing it.

### Ask for these

**Failing tests.** "Write me 15 JUnit cases for a nonogram line solver, including the nasty edge cases." Huge leverage, gives away nothing — you still have to make them pass, which is where the learning is. Getting good tests is also legitimately hard and you'd write worse ones right now.

**Concepts, when you're stuck.** "Why would a line solver return a wrong answer on a clue of `[0]`?" You get the idea; you write the fix.

**Shape before code.** "What data structure should hold the undo stack, and what are the tradeoffs?" Most of the actual engineering thinking lives in these decisions, and having them explained doesn't rob you of the implementation.

**Code review after the fact.** Write it, get it working, *then* ask what's wrong with it. This is the highest-value use in the whole list and it's the one people skip.

**Explanation of an error message.** Stack traces are a language you don't speak fluently yet. Translation is fine.

### Don't ask for these

**"Write the line solver."** Obviously.

**"Fix this bug."** Ask *why it's broken* instead. The debugging skill is worth more than the fix, and it's the skill that transfers to every job you'll ever have.

**"Is this right?"** — before running it. Run it. Tests answer this question better than I do, and building the reflex to reach for a test instead of an oracle is part of what you're here for.

**Anything at the "just this once, I'm tired" moment.** That moment is where the rule actually gets decided.

### A trick worth knowing

When you *do* want to see how an algorithm works, ask for it as **pseudocode** or **in a language you're not using for that layer**. You get the idea without a copy-paste path. Python pseudocode for a DP you're about to write in Java is close enough to learn from and far enough that you have to think.

---

## 3. Getting unstuck, in order

Work down this ladder. Don't skip to the bottom.

1. **Re-read the error message.** Actually read it. Line number, exception type, the whole thing.
2. **Print the state.** Print the grid, print the clue array, print the line before and after. Most nonogram bugs are visible the instant you look at the board.
3. **Shrink the case.** Does it fail on a 3×3? A single line? One clue? A bug you can reproduce in five cells takes ten minutes; the same bug in a 10×10 takes an hour.
4. **Write the test that reproduces it.** Now it's permanent, and you'll never ship that bug twice.
5. **Rubber-duck it in writing.** Type out what you expect to happen and what happens instead. This solves it maybe a third of the time before you finish typing.
6. **Twenty minutes, then ask** — for the concept, not the code.

The twenty-minute rule matters in both directions. Under twenty minutes, you're outsourcing thinking you were about to do anyway. Past an hour of no progress, you're not learning, you're grinding, and grinding is how projects get abandoned.

---

## 4. What each milestone actually teaches

Worth knowing, because if a step feels tedious this is the reason to push through it.

| Step | The real lesson |
|---|---|
| 1 — Clue derivation | Run-length encoding; how a bad index convention poisons everything downstream |
| 2 — Brute force solver | Combinatorial enumeration; writing the correct-but-slow version first as a reference |
| 3 — Propagation | Constraint propagation and fixed-point iteration — "loop until nothing changes" is everywhere in real software |
| 4 — Generator | Generate-and-test; why proving a property is different from hoping for it |
| 5 — Terminal game | Separating engine from interface. This is the architecture lesson of the project |
| 6 — JSON output | Serialization, and designing a format you'll have to read by eye for months |
| 7 — Web grid | DOM manipulation, event handling, the browser's coordinate system |
| 8 — Touch input | Pointer events, gesture state machines, why mobile input is genuinely hard |
| 9 — Persistence | State that outlives the process |
| 10 — PWA deploy | Service workers, caching, offline-first, what "shipping" means |
| 11 — DP solver | Dynamic programming and memoization, with a reference implementation to check against |

Step 11 is the one to look forward to. Writing a DP algorithm when you already have a slow correct version to test against is the single best way to learn dynamic programming, because you get instant, unambiguous feedback on whether you understood it.

---

## 5. Checkpoints

At the end of each milestone, close the editor and answer out loud. If you can't, you copied something without absorbing it — go back.

**After step 2:** Why does a clue of `[7]` in a 10-cell line force four cells filled, before you know anything else? Draw it.

**After step 3:** Why does solving rows and columns repeatedly find things that one pass misses?

**After step 4:** Why does "the propagation solver finished" prove the puzzle has exactly one solution?

**After step 5:** If you swapped the terminal interface for a web one, which files would change? (Correct answer: only the interface file. If it's more, your layering leaked.)

**After step 8:** Why does axis-lock exist? What does the interaction feel like without it? (Try it — actually remove it for a minute.)

**After step 10:** What does the service worker do when the phone has no signal, and how does it know what to serve?

---

## 6. Working habits

**Commit at every green test.** Small commits with real messages. You'll want the ability to go back to "the last version that worked" more often than you expect, and cultivating that reflex now costs nothing.

**Keep a `NOTES.md` in the repo.** One line each time you get stuck and unstick yourself: what broke, what fixed it. Ten minutes a week, and in three months it's the most valuable file in the project. It's also the thing you'll mine for interview stories.

**Play the game.** Regularly, on your actual phone, not the simulator. You are the only playtester you have, and things that are obviously wrong in your hand are invisible in a desktop browser.

**Set a pace you can hold.** Steps 1–5 are maybe a couple of weekends each at your level; steps 7–8 are the long ones because touch input has more edge cases than the entire solver. This is a several-month project. Treating it as a two-week sprint is how it dies at step 4.

---

## 7. Traps

**Rewriting step 1 because you learned something in step 6.** You will want to. Note it in `NOTES.md` and keep going. Finishing an imperfect version teaches more than perfecting an unfinished one.

**Starting with the DP solver.** It's the interesting algorithm, so it's tempting. You'll write it, it'll be subtly wrong, and you'll have nothing to test it against. That's the trap the brute-force version exists to prevent.

**Optimizing anything before step 11.** Every performance choice in the spec is deliberately naive with a documented trigger for revisiting. If you can't state which measurement justifies an optimization, don't do it.

**Building the settings screen.** It's comfortable, low-stakes work that produces a feeling of progress. So is picking colors. Do the hard thing first; the game has to be good before its options screen matters.

**Reaching for the App Store early.** Provisioning profiles, code signing, and review rejections are a genuinely miserable first experience of deployment, and none of it teaches you anything about your app. The PWA path (spec §10) gets it on your home screen this month.

---

## 8. Where to look things up

Not everything should come from an assistant. Building the habit of reading primary sources is part of the job.

- **Java** — the official Javadoc. `java.util.Deque`, `List`, `Arrays` are most of what this project needs.
- **JUnit 5** — the user guide's "Writing Tests" section; you need maybe 10% of it.
- **Web APIs** — MDN, always. Pointer Events, CSS Grid, localStorage, Service Worker API.
- **PWAs** — web.dev's PWA docs. Manifest and service worker basics, and the offline cookbook.
- **Nonogram theory** — the Wikipedia article, and search "nonogram solver line solving algorithm" for writeups of the DP approach. Read *about* it before you write it; don't read someone's Java implementation.

---

## 9. What "done" looks like

You've won when: the game is on your home screen, it works on the subway with no signal, your girlfriend plays it without asking how it works, and you can explain any file in the repo to a stranger without opening it.

The App Store, picture puzzles, and 15×15 grids are all still there afterward. They'll be much easier from that position than from this one.
