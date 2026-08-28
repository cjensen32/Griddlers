Good instinct, and the spec needs real changes to serve it — as written it optimizes for a finished product, which is a different thing.

## Change what you use me for

Four rules that keep the learning intact:

- Ask for **failing tests**, not implementations. "Write me 15 test cases for the line solver including the nasty edge cases" is huge leverage and gives away nothing — you still have to make them pass.
- Ask "why is this wrong" instead of "fix this." Paste the bug, get the concept, write the fix yourself.
- Ask for the *shape* before the code — data structures, function signatures, tradeoffs. That's where most of the actual engineering thinking lives, and it's the part a spec can hand you without stealing the work.
- When you do want code shown, ask for it in a language you're *not* using for that layer, or as pseudocode. You get the idea without a copy-paste path.

## Cut the duplicate solver entirely

The biggest complexity in that spec is maintaining rules in both Java and TypeScript. You can delete that problem: have the Java generator emit a **deduction sequence** with each puzzle — the ordered list of cells the propagation solver forced, and which line forced each one.

Then the TS side needs no solver at all. Hints just walk that list to the first cell the player hasn't got yet. Win check is comparing to the solution. That's it. Java owns all the algorithms, TS owns all the UI, no shared logic, no conformance fixtures needed.

## Toolchain cuts

| Spec says | Do instead | Why |
|---|---|---|
| base64-packed bitsets | plain JSON `["0110","1001"]` | readable in DevTools; packing is premature |
| canvas rendering | DOM divs, one per cell | inspectable, debuggable, fine at 10×10 |
| IndexedDB | localStorage | ~5 lines vs. ~50; swap later if you hit limits |
| multi-module Maven | one module, split packages | module wiring teaches you nothing here |
| TeaVM / Track B | ignore it | it's a trap for a first project |
| difficulty rating heuristic | hardcode Easy/Medium by grid size | the heuristic needs a corpus you don't have |

## Write the slow solver first

Before the DP: brute-force the line solver by enumerating every possible placement of the clues and intersecting them. It's maybe 30 lines, obviously correct, and unusably slow past ~15 cells.

Keep it forever. When you write the DP version, you have a reference implementation to test it against on random inputs — which is both a real technique and the moment the DP actually clicks.

## App Store: you probably shouldn't

iOS lets you install a PWA straight from Safari via Share → Add to Home Screen. Full screen, own icon, no browser chrome, works offline with a service worker. No $99/year, no Apple Developer account, no review, no Xcode, no provisioning profiles — which is a genuinely miserable first encounter with deployment.

Ship to a static host (Netlify/Vercel/GitHub Pages, all free), install it on your phone, play it for a month. If you still want it in the App Store after that, *then* pay the $99 and wrap it in Capacitor — and you'll be doing it with a working app instead of debugging code signing and game logic simultaneously.

## Revised milestones

1. Clue derivation + `Clues.from(solution)` — small, teaches the domain
2. Brute-force line solver + tests
3. Propagation loop → your first generated puzzle
4. Terminal-playable version in Java (print grid, type coordinates) — proves the engine before any UI exists
5. DP line solver, validated against #2
6. Static HTML/JS page that loads one puzzle JSON, click-to-fill only
7. Touch handling, drag painting, mode toggle
8. Service worker + manifest → installed on your phone

Steps 1–4 are pure Java and pure algorithms. Step 6 is where you find out how much frontend you actually know. Nothing before step 8 involves deployment at all.

Want me to rewrite the spec around this — learning-ordered, with the deduction-sequence format and the cuts folded in?
