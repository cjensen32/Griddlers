#!/usr/bin/env python3
"""Unwrap hard-wrapped Markdown prose into one physical line per logical line.

Default: consecutive prose lines inside a block are joined. A break is KEPT only
when it is structural (blank line, heading, table row, rule, fence, new list item)
or when it is a deliberate stack of short bold-led lines:

    **Version:** 0.6                     bold label + colon, next line also bold-led
    **Course:** ...
    **Tier 1 — every file.** ...         ends a sentence, next line also bold-led
    **Tier 2 — every flow.** ...

Every kept prose break is reported so it can be eyeballed; nothing else is silent.

    python3 .agents/tools/unwrap_markdown.py lessons/*.md            rewrite in place
    python3 .agents/tools/unwrap_markdown.py --check lessons/*.md    report only, exit 1 if any file would change

Do not run this over .agents/archive/**. That is a frozen record; see the Markdown
rule in .agents/reference/COURSE_STANDARDS.md.
"""
import re
import sys

FENCE = re.compile(r"^\s*(```|~~~)")  # any indent: fences nest inside list items
HEADING = re.compile(r"^\s{0,3}#{1,6}\s")
HRULE = re.compile(r"^\s{0,3}([-*_])(\s*\1){2,}\s*$")
TABLE = re.compile(r"^\s*\|")
LIST = re.compile(r"^(\s*)([-*+]|\d+[a-z]?[.)])\s+")
QUOTE = re.compile(r"^\s*>\s?")
INDENT_CODE = re.compile(r"^ {4,}\S")
QUOTE_BLANK = re.compile(r"^\s*>\s*$")
BOLD_LED = re.compile(r"^\s*(>\s?)?\*\*")
BOLD_LABEL = re.compile(r"^\s*(>\s?)?\*\*[^*]+:\*\*")
SENTENCE_END = re.compile(r"[.!?]['\"”’)]?\s*$")


def structural(line):
    return (not line.strip() or QUOTE_BLANK.match(line) or HEADING.match(line)
            or HRULE.match(line) or TABLE.match(line) or FENCE.match(line))


def keep_break(cur, nxt):
    """True when the break between cur and nxt is deliberate, not a hard wrap."""
    if structural(nxt) or LIST.match(nxt):
        return True, "structural"
    if QUOTE.match(cur) and not QUOTE.match(nxt):
        return True, "structural"
    if INDENT_CODE.match(nxt) and not LIST.match(cur) and not INDENT_CODE.match(cur):
        return True, "structural"
    if BOLD_LED.match(nxt):
        if BOLD_LABEL.match(cur):
            return True, "bold-label stack"
        if SENTENCE_END.search(cur):
            return True, "bold-led stack"
    return False, ""


def unwrap(lines):
    out, kept = [], []
    in_fence, fence_char = False, None
    i, n = 0, len(lines)
    while i < n:
        line = lines[i]
        m = FENCE.match(line)
        if m:
            if not in_fence:
                in_fence, fence_char = True, m.group(1)[0]
            elif line.strip()[0] == fence_char:
                in_fence = False
            out.append(line)
            i += 1
            continue
        if in_fence or structural(line) or (INDENT_CODE.match(line) and not LIST.match(line)):
            out.append(line)
            i += 1
            continue

        acc, j = line, i
        while j + 1 < n:
            stop, why = keep_break(lines[j], lines[j + 1])
            if stop:
                if why != "structural":
                    kept.append((j + 1, why, lines[j]))
                break
            tail = QUOTE.sub("", lines[j + 1]) if QUOTE.match(acc) else lines[j + 1]
            acc = acc.rstrip() + " " + tail.strip()
            j += 1
        out.append(acc)
        i = j + 1
    return out, kept


def main(argv):
    check = "--check" in argv
    dirty = 0
    for path in [a for a in argv if not a.startswith("--")]:
        original = open(path, encoding="utf-8").read().split("\n")
        result, kept = unwrap(original)
        changed = result != original
        if changed and not check:
            open(path, "w", encoding="utf-8").write("\n".join(result))
        print(f"{'rewrapped' if changed else 'unchanged':>10}  {path}  "
              f"({len(original)} -> {len(result)} lines)")
        dirty += 1 if changed else 0
        for lineno, why, text in kept:
            print(f"            kept break [{why}] {path}:{lineno}  ...{text[-56:]!r}")
    return 1 if (check and dirty) else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
