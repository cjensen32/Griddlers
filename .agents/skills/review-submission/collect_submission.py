#!/usr/bin/env python3
"""Collect everything a submission review has to look at, before the reviewer says a word.

Reads the open chapter's README, the submitted lesson file, the journal, Git, the
build reports, and the test ladder, then prints one report. It asserts nothing about
quality - it gathers the evidence a reviewer would otherwise gather by hand and get
half of. Nothing here writes to the repository, and nothing here reads src/ for
anything but file names and class names.

    python3 .agents/skills/review-submission/collect_submission.py             the lesson marked current
    python3 .agents/skills/review-submission/collect_submission.py 2.1         a named lesson
    python3 .agents/skills/review-submission/collect_submission.py --build     run mvn verify first
    python3 .agents/skills/review-submission/collect_submission.py --capstone  chapter gates instead of one lesson
"""
import os
import re
import subprocess
import sys

ROW = re.compile(r"^\s*\|(.+)\|\s*$")
BOX = re.compile(r"^\s*-\s*\[( |x|X)\]\s*(.*)$")
NUMBERED = re.compile(r"^(\d+)\.\s+(.*)$")
HEADING = re.compile(r"^(#{1,6})\s+(.*)$")
LINK = re.compile(r"\[[^\]]*\]\(([^)]+)\)")
CONFIDENCE = re.compile(r"\((\d{1,3})%\s*confident\)")
SUREFIRE = re.compile(r"Tests run: (\d+), Failures: (\d+), Errors: (\d+), Skipped: (\d+)")
MARK = re.compile(r"\?\d+")
MUST = ("Must be true", "Must exist", "Must happen")  # the section has been renamed before


def root():
    """The repository root, so the script works from any working directory."""
    found = subprocess.run("git rev-parse --show-toplevel", shell=True, capture_output=True, text=True)
    return found.stdout.strip() or os.getcwd()


def run(command, cwd=None):
    p = subprocess.run(command, shell=True, cwd=cwd or REPO, capture_output=True, text=True)
    return p.stdout + p.stderr


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read().split("\n")


def cells(line):
    return [c.strip() for c in ROW.match(line).group(1).split("|")]


def section(lines, title):
    """The body lines under the heading whose text starts with title, exclusive of the next heading."""
    out, taking, depth = [], False, 0
    for line in lines:
        h = HEADING.match(line)
        if h and taking and len(h.group(1)) <= depth:
            break
        if h and h.group(2).lower().startswith(title.lower()):
            taking, depth = True, len(h.group(1))
            continue
        if taking:
            out.append(line)
    return out


def head(title):
    print("\n" + title)
    print("-" * len(title))


def open_chapter():
    """The chapter directory the syllabus marks open, falling back to the newest one on disk."""
    lines = read(os.path.join(REPO, "lessons", "SYLLABUS.md"))
    for line in lines:
        if "*(open)*" in line:
            for target in LINK.findall(line):
                if target.endswith("README.md"):
                    return os.path.join(REPO, "lessons", os.path.dirname(target))
    chapters = sorted(d for d in os.listdir(os.path.join(REPO, "lessons")) if d.startswith("ch"))
    return os.path.join(REPO, "lessons", chapters[-1]) if chapters else None


def lesson_rows(chapter):
    """(number, title, hours, state, done, file) per row of the chapter README's lesson table."""
    lines = read(os.path.join(chapter, "README.md"))
    rows, columns = [], None
    for line in lines:
        if not ROW.match(line):
            columns = None
            continue
        c = cells(line)
        if columns is None:
            if c[0] == "#":
                columns = c
            continue
        if set("".join(c)) <= set("-: "):
            continue
        row = dict(zip(columns, c))
        target = LINK.search(row.get("Lesson", ""))
        row["file"] = os.path.join(chapter, target.group(1)) if target else None
        rows.append(row)
    return rows


def boxes(lines):
    """(checked, text) for every checkbox in these lines."""
    return [(m.group(1).lower() == "x", m.group(2)) for m in (BOX.match(l) for l in lines) if m]


def report_boxes(lines, title):
    marks = boxes(section(lines, title))
    if not marks:
        print("  %-28s no checkboxes found" % (title + ":"))
        return
    done = sum(1 for checked, _ in marks if checked)
    print("  %-28s %d/%d checked" % (title + ":", done, len(marks)))
    for checked, text in marks:
        if not checked:
            print("      unchecked: " + text)


def quiz(lines):
    """(number, question, answer lines) for each numbered question under the pop quiz heading."""
    body, out = section(lines, "Pop quiz"), []
    for n, line in enumerate(body):
        m = NUMBERED.match(line)
        if not m:
            continue
        answer = []
        for later in body[n + 1:]:
            if NUMBERED.match(later) or HEADING.match(later):
                break
            if later.strip():
                answer.append(later.strip())
        out.append((m.group(1), m.group(2), answer))
    return out


def definitions(lesson_number):
    """Journal Definitions rows for this lesson, plus rows anywhere with an empty cell."""
    lines = read(os.path.join(REPO, "lessons", "JOURNAL.md"))
    mine, blank, columns = [], [], None
    for line in section(lines, "Definitions"):
        if not ROW.match(line):
            continue
        c = cells(line)
        if columns is None:
            columns = c
            continue
        if set("".join(c)) <= set("-: "):
            continue
        row = dict(zip(columns, c))
        if row.get("Lesson") == lesson_number:
            mine.append(row)
        if not row.get("Scope") or not row.get("Definition"):
            blank.append(row)
    return mine, blank


def open_gaps():
    """Journal `Open gaps` rows, grouped by the lesson that left them open."""
    lines = read(os.path.join(REPO, "lessons", "JOURNAL.md"))
    out, columns = {}, None
    for line in section(lines, "Open gaps"):
        if not ROW.match(line):
            continue
        c = cells(line)
        if columns is None:
            columns = c
            continue
        if set("".join(c)) <= set("-: "):
            continue
        row = dict(zip(columns, c))
        out.setdefault(row.get("Lesson", ""), []).append(row)
    return out


def ladder():
    """Test files by tier, and production classes with no tier 1 file of their own."""
    tiers, production = {1: [], 2: [], 3: []}, []
    for base, tier_of in ((os.path.join(REPO, "src", "test"), True), (os.path.join(REPO, "src", "main"), False)):
        for where, _, files in os.walk(base) if os.path.isdir(base) else []:
            for name in sorted(files):
                if not name.endswith(".java"):
                    continue
                path = os.path.relpath(os.path.join(where, name), REPO)
                if not tier_of:
                    production.append(path)
                elif name.endswith("ProcessTest.java"):
                    tiers[3].append(path)
                elif name.endswith("FlowTest.java"):
                    tiers[2].append(path)
                elif name.endswith("Test.java"):
                    tiers[1].append(path)
    covered = {os.path.basename(p)[:-len("Test.java")] for p in tiers[1]}
    missing = [p for p in production if os.path.basename(p)[:-len(".java")] not in covered]
    return tiers, production, missing


def surefire():
    """Per-class results from the last test run, and whether that run predates the newest source file."""
    reports = os.path.join(REPO, "target", "surefire-reports")
    if not os.path.isdir(reports):
        return [], None
    results, newest_report = [], 0
    for name in sorted(os.listdir(reports)):
        if not name.endswith(".txt"):
            continue
        path = os.path.join(reports, name)
        newest_report = max(newest_report, os.path.getmtime(path))
        for line in read(path):
            m = SUREFIRE.search(line)
            if m:
                results.append((name[:-4], m.group(0)))
                break
    newest_source = 0
    for where, _, files in os.walk(os.path.join(REPO, "src")) if os.path.isdir(os.path.join(REPO, "src")) else []:
        for name in files:
            newest_source = max(newest_source, os.path.getmtime(os.path.join(where, name)))
    return results, (newest_source > newest_report)


REPO = root()
args = [a for a in sys.argv[1:] if not a.startswith("--")]
flags = {a for a in sys.argv[1:] if a.startswith("--")}
chapter = open_chapter()
rows = lesson_rows(chapter)

print("Repository: " + REPO)
print("Chapter:    " + os.path.relpath(chapter, REPO))

head("Chapter state")
written = [r for r in rows if r.get("State") in ("current", "queued")]
for r in rows:
    if r.get("#") in ("", None):
        continue
    print("  %-4s %-10s hours %-6s %s" % (r.get("#"), r.get("State", "?"), r.get("Hours") or "-", r.get("Lesson", "")))
if len(written) > 2:
    print("  ! more than two lessons are written at once")
for r in rows:
    if r.get("State") == "green" and not (r.get("Hours") or "").strip():
        print("  ! lesson %s is green with no hours recorded" % r.get("#"))

if "--capstone" in flags:
    head("Capstone - Must be true")
    capstone = os.path.join(chapter, "CAPSTONE.md")
    guarantees = [cells(l)[0] for l in read(capstone) if ROW.match(l)][2:]
    for n, g in enumerate(guarantees, 1):
        print("  %d. %s" % (n, g))
    print("\n  Gate 2 asks the learner to name the test covering each of the %d rows above." % len(guarantees))
    print("  Every lesson must be green before this file is written past its list.")
    unfinished = [r.get("#") for r in rows if r.get("State") not in ("green", None, "") and r.get("#") != "★"]
    if unfinished:
        print("  ! lessons not green: " + ", ".join(str(u) for u in unfinished))
    blank_hours = [r.get("#") for r in rows if r.get("#") not in ("", None, "★") and not (r.get("Hours") or "").strip()]
    if blank_hours:
        print("  ! hours not recorded for: " + ", ".join(str(b) for b in blank_hours))
    head("Journal - capstone gap counts")
    for line in section(read(os.path.join(REPO, "lessons", "JOURNAL.md")), "Capstone gap counts"):
        if ROW.match(line):
            print("  " + line.strip())
    print("  Gate 3's count is recorded here by the learner once the gap analysis is done.")
else:
    number = args[0] if args else next((r.get("#") for r in rows if r.get("State") == "current"), None)
    row = next((r for r in rows if r.get("#") == number), None)
    if not row or not row.get("file"):
        sys.exit("No lesson file for %r. Pass a number from the table above." % number)
    lines = read(row["file"])
    head("Lesson %s - %s" % (number, os.path.relpath(row["file"], REPO)))
    found = next((m for m in MUST if section(lines, m)), None)
    if found:
        report_boxes(lines, found)
    else:
        print("  ! no %s section - renamed? its boxes are not being reviewed" % " / ".join(MUST))
    report_boxes(lines, "Done when")
    for title in ("Mastery", "Reinforce", "Clarify"):
        if section(lines, title):
            report_boxes(lines, title)

    head("Pop quiz")
    for n, question, answer in quiz(lines):
        stated = CONFIDENCE.findall(" ".join(answer))
        print("  %s. %s" % (n, "answered" if answer else "UNANSWERED"), end="")
        print((" - stated confidence " + ", ".join(c + "%" for c in stated)) if stated else "")
        if not answer:
            print("      " + question)
    print("\n  Stated confidence is context, never a routing signal. Judge each answer on what it says.")

    head("Journal - Definitions")
    mine, blank = definitions(number)
    print("  %d row(s) tagged %s" % (len(mine), number))
    for entry in mine:
        print("    %-28s %-8s %s" % (entry.get("Term", ""), entry.get("Scope", ""), entry.get("Definition", "")))
    if blank:
        print("  %d row(s) anywhere in the table still have an empty Scope or Definition" % len(blank))
    flagged = [e for e in mine if MARK.search(e.get("Definition", ""))]
    if flagged:
        print("  %d row(s) carry a ?n mark and are still open:" % len(flagged))
        for entry in flagged:
            print("    %-28s %s" % (entry.get("Term", ""), MARK.search(entry["Definition"]).group(0)))
        print("  A ?n row is settled by the learner rewriting it, then deleting the marker.")

    head("Journal - open gaps")
    gaps = open_gaps()
    if not gaps:
        print("  no Open gaps table in the journal")
    else:
        for entry in gaps.get(number, []):
            print("    %-6s %s -> settles in %s" % (entry.get("Kind", ""), entry.get("Open item", ""), entry.get("Settles in", "")))
        stated = (row.get("Open") or "").strip()
        counted = len(gaps.get(number, []))
        if stated and stated not in ("-", "\u2014"):
            if not stated.isdigit() or int(stated) != counted:
                print("  ! README Open says %s for %s, the table holds %d row(s)" % (stated, number, counted))
        elif counted:
            print("  ! %d gap row(s) for %s, but the README Open cell is empty" % (counted, number))
        for lesson, entries in sorted(gaps.items()):
            if lesson != number:
                print("  %s still carries %d open gap(s)" % (lesson, len(entries)))

head("Git")
print(run("git status --short") or "  clean")
print("  " + run("git log --oneline -5").replace("\n", "\n  ").rstrip())
check = run("git diff --check")
print("  git diff --check: " + ("clean" if not check.strip() else "\n" + check))

head("Build")
if not os.path.exists(os.path.join(REPO, "pom.xml")):
    print("  no pom.xml - the build does not exist yet")
else:
    if "--build" in flags:
        out = run("mvn -B verify")
        passed = "BUILD SUCCESS" in out
        print("  mvn -B verify: " + ("BUILD SUCCESS" if passed else "BUILD FAILURE"))
        interesting = [l for l in out.split("\n") if ("ERROR" in l or "WARNING" in l) and l.split("]", 1)[-1].strip()]
        if not passed and not interesting:
            interesting = [l for l in out.split("\n") if l.strip()][-15:]  # it died before it could log
        for line in interesting:
            print("    " + line.strip()[:220])
        if passed:
            print("    A green build is not a correct POM. Read every WARNING above.")
    results, stale = surefire()
    for name, line in results:
        print("  %-48s %s" % (name, line))
    if not results:
        print("  no surefire reports - tests have not run")
    elif stale:
        print("  ! reports are older than the newest file under src/ - rerun with --build")

head("Test ladder")
tiers, production, missing = ladder()
for tier in (1, 2, 3):
    print("  tier %d: %s" % (tier, ", ".join(os.path.basename(p) for p in tiers[tier]) or "none"))
print("  %d production class(es), %d with no tier 1 file:" % (len(production), len(missing)))
for path in missing:
    print("    " + path)
print("\n  Name a gap. Never fill one. See .agents/CONTEXT.md, Agent constraints.")

head("Documentation checks")
touched = ["lessons/JOURNAL.md", os.path.relpath(os.path.join(chapter, "README.md"), REPO)]
if "--capstone" not in flags:
    touched.insert(0, os.path.relpath(row["file"], REPO))
print(run("python3 .agents/tools/unwrap_markdown.py --check " + " ".join(touched)).rstrip())
print(run("python3 .agents/tools/check_midsentence.py " + " ".join(touched)).rstrip())
print("\n  --check only. Never rewrite JOURNAL.md with the unwrapper; its !1-style footnotes are prose lines it will join.")
print("\nProcedure: .agents/skills/review-submission/SKILL.md")
