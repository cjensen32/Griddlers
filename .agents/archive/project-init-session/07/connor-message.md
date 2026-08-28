# Turn 07 — Loosening the front, sharpening the end

## TRANSCRIPT - RESPONSE
```md
Some notes I had while you were on turn 6: to make it less strict at the beginning and more gap analysis at the end. The CAPSTONE.md and CAPSTONE-test.java sound be bare/architecture only. The job of the agent is to create a test and capstone at the end of the chapter rather than the start. I think having a more open mindset with the endgoal of each chapter should help to resolve issues with the capstone steps and shape problems I had with the first chapter.

The problems were more with me having to shape my code to fit the box of the capstone test which led to less responsibility for inital testing and responsibility for the code shape and ask more for help than I needed or should have needed. Again the push to write tests and understand them is most important in the next project as if I had written effective and comprehensive tests up to that point, failures would happen less frequently, I would have less "head scratch" moments that don't help me learn anything.

Now after turn 06 - Notes from the ./Nonogram-Spec-Creation-Chat/06/HANDOFF.md file, section 4.:
1. Can you explain this simply why this must be true, why no java.io and what depedency direction needs to be rewritten/resolved.
2. For the browser based sections/code, what would be the best way to write tests for those sections?
  - Keep the ideas you have for the later chapters established, but nothing beyond the initial first few (2-3 chapters) should be established so far as being a log line that would be found in a syllabus saying "Ch Y: will be covering X".
  - Of course if a later course chapter disagrees or flat out is incompatable with the current shape and where the chapter/next-chapter are headed, then flag and we will discuss
  - **IMPORTANT CONTEXT THAT AFFECTS ALL CONTEXT/DOCUMENTS/SCOPE/INTENTION OF PROJECT**: Chapters and lessons should be planned out, but I want only the current chapter/lesson to be set in stone, where the capstone is created AFTER completing all previous lessons, the CAPSTONE and future chapters/lessons should live as manifests contain minimal content about implementation and instead choose to focus on the contract of things that **HAVE TO BE TRUE FOR FUTURE LESSONS AND CHAPTERS TO SUCCEED**, nothing more.
    - Also important, test writing, should be exclusively completed by the student. and the capstone test should rely on the tests that I have written to validate the code coverage, effectiveness, and performance. it doesn't strictly speaking have to be a "physical" java test file. it can be a "Hey agent, can I have you grade my work for chapter X for the CAPSTONE?" where you will then either execute the java test, or give feedback/analysis of my code back to me so I can correct/fix/improve my code to get it up to expected standards.
3. For the chapter 2 contract tests, the initial tests can be barebones and it is expected that they are, functionality and more rigorous testing come with additional features/flows and shouldn't be expected when writing the barebones boilerplate.
  - it is your job to help me organize those ideas of tests effectively into files.
  - Lessons' test writing segments may include going back to previous test files to do rewriting, fixing, appending, modifying, or deleting
  - Shouldn't be keeping around ineffective/outdated/trivial tests for any longer than they are needed.
  - This shouldn't modify the rule of what is suggested for you to write out in the user facing files.
  - remember that I want to be writing the tests, and the files, but knowing what the concepts look like is harder to understand without examples, write an example, not solution ALWAYS
4. This is true, I don't expect this to be committed, it is an appendix that's only integration with the codebase here in the job-application-tracker is a small note in the @README.md file that points to the new project once I am finished with the handoff.
  - Still need to name the project and am taking suggestions if you have any.

Finally make sure that the files living at the base of `Nonogram-Spec-Creation-Chat`, the `Nonogram-Spec-Creation-Chat/nonogram-learning-guide-FINAL.md`, `Nonogram-Spec-Creation-Chat/nonogram-spec-FINAL.md`, and any other newly introduced documentation that has versionings (HANDOFF.md, CHAPTER-1-REVIEW.md) is here as a `...-FINAL.md` version of itself.
```

## The problem with how Chapter 1 was structured

The capstone and its test existed before the lessons did. That meant shaping my code to fit the box
the capstone test defined, which had three costs: less responsibility for initial testing, less
responsibility for the shape of my own code, and asking for more help than I needed or should have
needed.

Make it less strict at the beginning and more gap analysis at the end.

## The structural rule that affects everything

Chapters and lessons should be planned out, but **only the current chapter/lesson is set in stone.**
The capstone is created AFTER completing all previous lessons. Future chapters and lessons live as
**manifests** containing minimal content about implementation, focused instead on the contract of
things that **have to be true for future lessons and chapters to succeed** — nothing more.

Beyond the first two or three chapters, nothing should be more established than a syllabus log line:
"Ch Y: will be covering X." If a later chapter disagrees with or is incompatible with the current
shape, flag it and we discuss.

## Testing

Test writing is completed exclusively by the student. The capstone relies on the tests I have
written, to validate coverage, effectiveness, and performance. It does not strictly have to be a
physical Java test file — it can be "Hey agent, can I have you grade my work for Chapter X?", where
the agent either executes my tests or gives feedback and analysis on my code so I can correct, fix,
and improve it up to expected standards.

The push to write tests and understand them is the most important thing in the next project. Had I
written effective and comprehensive tests up to this point, failures would happen less often and I
would have fewer head-scratch moments that teach me nothing.

## On barebones tests

Initial tests can be barebones and it is expected that they are. Functionality and more rigorous
testing come with additional features and flows; they should not be expected when writing barebones
boilerplate.

- It is the agent's job to help organize test ideas effectively into files.
- Lessons' test-writing segments may include going back to previous test files to rewrite, fix,
  append, modify, or delete.
- Ineffective, outdated, or trivial tests should not be kept around longer than they are needed.
- None of this modifies the rule about what the agent may write into user-facing files.
- I want to be writing the tests and the files, but concepts are harder to understand without
  examples. **Write an example, not a solution. Always.**

## Questions answered this turn

1. Why must the engine avoid `java.io`, and what dependency direction needs resolving?
2. What is the best way to test the browser-based chapters?

## Housekeeping

Not committed. This is an appendix whose only integration with `job-application-tracker` is a small
note in `README.md` pointing at the new project, added once the handoff is finished.

The base of `Nonogram-Spec-Creation-Chat/` holds a `...-FINAL.md` of every versioned document.

Project still needs a name.
