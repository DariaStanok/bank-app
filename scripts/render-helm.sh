#!/usr/bin/env bash
set -euo pipefail

# Render umbrella chart with selected values file
CHART="charts/umbrella"
VALUES="${1:-charts/umbrella/values.yaml}"
helm template bank-app "$CHART" -f "$VALUES"
