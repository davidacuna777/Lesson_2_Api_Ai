#!/usr/bin/env bash
set -euo pipefail
ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"
NAME="leccion2-servidor-factory-deepseek-telegram.zip"
rm -f "$NAME"
zip -r "$NAME" . -x "*/target/*" -x "*/.git/*" -x "*/.idea/*"
echo "Created $NAME"
