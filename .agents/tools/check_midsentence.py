#!/usr/bin/env python3
"""Flag Markdown prose lines that end mid-sentence - the tell that a file was hard-wrapped.

Secondary to unwrap_markdown.py --check, which catches wrapping comprehensively.
List items and table rows are their own logical lines and are not flagged.

    python3 .agents/tools/check_midsentence.py lessons/*.md
"""
import re, sys
sys.path.insert(0, __file__.rsplit("/", 1)[0])
from unwrap_markdown import FENCE, HEADING, HRULE, TABLE, LIST, QUOTE_BLANK, INDENT_CODE, frontmatter

OK_END = re.compile(r"""[.!?:;)\]}|*`"'”’—-]\s*$""")
found = 0
for path in sys.argv[1:]:
    in_fence, fence_char = False, None
    lines = open(path, encoding="utf-8").read().split("\n")
    front = frontmatter(lines)
    for n, line in enumerate(lines, 1):
        if n <= front: continue  # frontmatter is data, not prose
        m = FENCE.match(line)
        if m:
            if not in_fence: in_fence, fence_char = True, m.group(1)[0]
            elif line.strip()[0] == fence_char: in_fence = False
            continue
        if in_fence or not line.strip(): continue
        if HEADING.match(line) or HRULE.match(line) or TABLE.match(line): continue
        if QUOTE_BLANK.match(line) or INDENT_CODE.match(line): continue
        if LIST.match(line): continue  # a list item is its own logical line; hard wraps are caught by unwrap --check
        if not OK_END.search(line):
            found += 1
            print(f"{path}:{n}: ends mid-sentence -> ...{line[-64:]!r}")
print(f"\n{found} line(s) end mid-sentence")
sys.exit(1 if found else 0)
