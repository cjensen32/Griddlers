Spec is ready. Three things in it I'd flag as the load-bearing decisions:

**Java can't run on iOS**, so the spec proposes using Java where it's genuinely strong — puzzle generation, uniqueness proving, difficulty rating — and shipping a React/TS runtime for the actual on-device game. Given your Vite/React 19 work that's the shortest path to something on your phone. The alternative (TeaVM compiling Java to JS) is in there too, and the core module constraints keep both options open until M2.

**Uniqueness gate.** A puzzle only ships if a line-propagation solver can finish it with zero branching. That one rule buys you both a unique solution and "no guessing required," which is the main thing separating good picross apps from bad ones.

**Sizing math kills 15×15 at launch.** On a 390pt screen, 15×15 needs pan/zoom and sticky clue headers — a large chunk of hard UI. 5×5 and 10×10 fit without any of it.
[nonogram-spec.md](./nonogram-spec-v1.md)
