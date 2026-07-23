#!/usr/bin/env python3
"""Generate a shields.io endpoint badge JSON from the merged Kover XML report.

Reads LINE coverage from the aggregated report (build/reports/kover/report.xml by
default) and writes a shields.io endpoint object to docs/badges/coverage.json.

The Kover config (build-logic KoverConventionPlugin) already excludes UI and
generated code, so this figure is the hand-written *logic* coverage — hence the
"logic coverage" label. Usage:

    ./gradlew koverXmlReport
    python3 scripts/kover_badge.py
"""
import json
import sys
import xml.etree.ElementTree as ET
from pathlib import Path

REPORT = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("build/reports/kover/report.xml")
OUT = Path(sys.argv[2]) if len(sys.argv) > 2 else Path("docs/badges/coverage.json")


def line_percent(report: Path) -> float:
    root = ET.parse(report).getroot()
    for counter in root.findall("counter"):
        if counter.get("type") == "LINE":
            missed = int(counter.get("missed"))
            covered = int(counter.get("covered"))
            total = missed + covered
            return 100.0 * covered / total if total else 0.0
    raise SystemExit(f"No LINE counter found in {report}")


def color_for(pct: float) -> str:
    if pct >= 80:
        return "brightgreen"
    if pct >= 70:
        return "green"
    if pct >= 60:
        return "yellowgreen"
    if pct >= 50:
        return "yellow"
    if pct >= 40:
        return "orange"
    return "red"


def main() -> None:
    if not REPORT.exists():
        raise SystemExit(f"Kover report not found at {REPORT}. Run ./gradlew koverXmlReport first.")
    pct = line_percent(REPORT)
    badge = {
        "schemaVersion": 1,
        "label": "logic coverage",
        "message": f"{round(pct)}%",
        "color": color_for(pct),
    }
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(badge, indent=2) + "\n")
    print(f"{OUT}: {badge['message']} ({badge['color']})  [{pct:.2f}%]")


if __name__ == "__main__":
    main()
