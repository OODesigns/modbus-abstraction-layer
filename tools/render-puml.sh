#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
plantuml_jar="${PLANTUML_JAR:-$repo_root/plantuml.jar}"
shopt -s nullglob
diagrams=("$repo_root"/designs/*.puml)

if [[ ! -f "$plantuml_jar" ]]; then
    printf 'PlantUML jar not found: %s\n' "$plantuml_jar" >&2
    printf 'Set PLANTUML_JAR or place plantuml.jar in the repository root.\n' >&2
    exit 1
fi

if ! command -v java >/dev/null 2>&1; then
    printf 'Java is required to run PlantUML.\n' >&2
    exit 1
fi

if ! command -v dot >/dev/null 2>&1; then
    printf 'Graphviz is required to render PlantUML diagrams.\n' >&2
    exit 1
fi

if [[ ${#diagrams[@]} -eq 0 ]]; then
    printf 'No PlantUML diagrams found in %s/designs.\n' "$repo_root" >&2
    exit 1
fi

java -jar "$plantuml_jar" -failfast2 -tpng "${diagrams[@]}"