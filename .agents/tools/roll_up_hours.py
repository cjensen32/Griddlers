#!/usr/bin/env python3
"""Sum a chapter's per-lesson Hours and carry the total to the syllabus course map.

The chapter README owns the per-lesson numbers; the learner types those. This adds
them up, writes the sum into the README's own **Total** row, and copies the same
number into that chapter's Hours cell in lessons/SYLLABUS.md, so the two tables
cannot drift. Blank and em-dash cells count as zero and are simply not yet filled.

    python3 .agents/tools/roll_up_hours.py lessons/ch02-*/README.md            rewrite in place
    python3 .agents/tools/roll_up_hours.py --check lessons/ch02-*/README.md    report only, exit 1 if either file would change
"""
import os
import re
import sys

ROW = re.compile(r"^\s*\|(.+)\|\s*$")


def cells(line):
    return [c.strip() for c in ROW.match(line).group(1).split("|")]


def repad(line, index, value):
    """Replace one cell, keeping the row's existing column widths."""
    parts = line.split("|")
    width = len(parts[index + 1]) - 2
    parts[index + 1] = " " + value.ljust(width) + " "
    return "|".join(parts)


def table(lines, first_column):
    """Yield (line number, cells) for the body rows of the table headed by first_column."""
    head = next(n for n, l in enumerate(lines) if ROW.match(l) and cells(l)[0] == first_column)
    columns = cells(lines[head])
    for n in range(head + 2, len(lines)):
        if not ROW.match(lines[n]):
            break
        yield n, columns, cells(lines[n])


def number(text):
    try:
        return float(text.replace("*", ""))
    except ValueError:
        return 0.0


def main(paths, check):
    stale = 0
    for readme in paths:
        lines = open(readme, encoding="utf-8").read().split("\n")
        rows = list(table(lines, "#"))
        hours = next(c.index("Hours") for _, c, _ in rows[:1])
        lesson = next(c.index("Lesson") for _, c, _ in rows[:1])

        total = sum(number(r[hours]) for _, _, r in rows if "**Total**" not in r[lesson])
        chapter = next(r[0] for _, _, r in rows if "." in r[0]).split(".")[0]
        shown = f"{total:g}"

        line, _, row = next(r for r in rows if "**Total**" in r[2][lesson])
        want = repad(lines[line], hours, f"**{shown}**")
        stale += write(readme, lines, line, want, check)

        syllabus = os.path.normpath(os.path.join(os.path.dirname(readme), "..", "SYLLABUS.md"))
        slines = open(syllabus, encoding="utf-8").read().split("\n")
        srows = list(table(slines, "Ch"))
        scol = next(c.index("Hours") for _, c, _ in srows[:1])
        line, _, row = next(r for r in srows if r[2][0] == chapter)
        want = repad(slines[line], scol, shown)
        stale += write(syllabus, slines, line, want, check)

    print(f"\n{stale} line(s) out of date")
    return 1 if (check and stale) else 0


def write(path, lines, n, want, check):
    if lines[n] == want:
        print(f" current   {path}:{n + 1}  {want.strip()}")
        return 0
    print(f" {'stale  ' if check else 'updated'}   {path}:{n + 1}  {want.strip()}")
    if not check:
        lines[n] = want
        open(path, "w", encoding="utf-8").write("\n".join(lines))
    return 1


if __name__ == "__main__":
    args = [a for a in sys.argv[1:] if a != "--check"]
    sys.exit(main(args, "--check" in sys.argv[1:]))
