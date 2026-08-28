# Turn 09 — Transfer checklist

Given that everything is encapsulated inside this directory, will I be able to copy the folder into
a blank project and have all context transferred with it?

Will I be able to tell an agent to construct the agent architecture and have it do that effectively,
or is there other context I should be transferring over to the new project (configs, codestyles,
gitignore, etc)?

## Decisions

Rather than a starter kit of pre-copied files: just tell me which files should be transferred. I
will use `/init` to update `CONTEXT.md` as needed. Keep the files here minimal and in a shape that
can be deduced from context, rather than a twenty-file list of things to implement first — that
makes it easier to check and verify against what I already have.

Then: rewrite the checklist as Markdown, and include all content that is expected to change directly
in the file rather than describing it. Where a name or path relies on `project.env`, state the
variable explicitly.
