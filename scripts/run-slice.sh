#!/usr/bin/env bash
#
# run-slice.sh — driver do workflow de slices do FilmesFlix-Workspace.
#
# Uso:
#   ./scripts/run-slice.sh <NNN>           # ex: ./scripts/run-slice.sh 005
#   SLICE_JDK17=/path/to/jdk17 \
#     ./scripts/run-slice.sh 005           # JDK explícito (ver AGENTS.md)
#
# O que o script faz:
#   1. Localiza docs/slices/NNN-*.md e lista os checkpoints numerados.
#   2. Para cada checkpoint: exibe o título, aguarda o trabalho do agente/humano
#      (pause para revisão) e roda as verificações do AGENTS.md.
#   3. Mede o tempo de cada checkpoint e, ao final, emite um bloco pronto para
#      colar na seção "Retrospectiva" da spec.
#
# Ele NÃO executa o trabalho do checkpoint (isso é do agente/humano) — apenas
# orquestra, verifica e mede.
set -euo pipefail

WORKSPACE_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$WORKSPACE_ROOT"

JDK17="${SLICE_JDK17:-/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home}"

# ---------------------------------------------------------------- helpers ---
log()  { printf '\033[1;34m==>\033[0m %s\n' "$*"; }
warn() { printf '\033[1;33m[warn]\033[0m %s\n' "$*" >&2; }
fail() { printf '\033[1;31m[fail]\033[0m %s\n' "$*" >&2; exit 1; }

now() { python3 -c 'import time; print(int(time.time()))'; }

fmt_duration() { # segundos -> "Xm Ys"
  local s=$1
  printf '%dm %ds' $((s / 60)) $((s % 60))
}

run_check() { # roda um comando, mede, não aborta no primeiro erro
  local label=$1; shift
  log "verificação: $label"
  local start; start=$(now)
  if "$@" > /tmp/slice-check.log 2>&1; then
    printf '  \033[1;32mPASS\033[0m %s (%s)\n' "$label" "$(fmt_duration $(($(now) - start)))"
    return 0
  else
    printf '  \033[1;31mFAIL\033[0m %s (%s) — últimas linhas:\n' "$label" "$(fmt_duration $(($(now) - start)))"
    tail -15 /tmp/slice-check.log >&2
    return 1
  fi
}

verify_all() { # bateria completa de verificações do AGENTS.md
  local failures=0
  run_check "contrato: redocly lint" npx --yes @redocly/cli@1 lint contracts/openapi.yaml || failures=$((failures + 1))
  run_check "BFF: test + coverage" env JAVA_HOME="$JDK17" ./gradlew -p services/filmes-api test jacocoTestCoverageVerification || failures=$((failures + 1))
  if [ -d apps/filmes-flix/app ]; then
    run_check "app: unit tests + coverage" env JAVA_HOME="$JDK17" ./gradlew -p apps/filmes-flix testDebugUnitTest jacocoTestReport || failures=$((failures + 1))
  else
    warn "submodule apps/filmes-flix não inicializado — pulando testes do app"
  fi
  return $failures
}

# --------------------------------------------------------------- argparse ---
SLICE_NUM="${1:-}"
[[ "$SLICE_NUM" =~ ^[0-9]{3}$ ]] || fail "uso: $0 <NNN>  (ex: $0 005)"

SPEC=$(ls docs/slices/"$SLICE_NUM"-*.md 2>/dev/null | head -1 || true)
[[ -n "$SPEC" ]] || fail "spec não encontrada: docs/slices/$SLICE_NUM-*.md"

log "spec: $SPEC"
grep -E "^#{1,2} " "$SPEC" | head -3

# extrai checkpoints: linhas "1. ..." dentro da seção Checkpoints
# (sem mapfile: macOS ainda traz bash 3.2)
CHECKPOINTS=()
while IFS= read -r line; do CHECKPOINTS+=("$line"); done < <(awk '
  /^## Checkpoints/ {in_cp=1; next}
  /^## / && in_cp {in_cp=0}
  in_cp && /^[0-9]+\. / {sub(/^[0-9]+\. /, ""); print}
' "$SPEC")

if [ ${#CHECKPOINTS[@]} -eq 0 ]; then
  warn "nenhum checkpoint numerado encontrado em '## Checkpoints' — rodando só a verificação final"
fi

echo
log "${#CHECKPOINTS[@]} checkpoint(s) detectado(s):"
for i in "${!CHECKPOINTS[@]}"; do printf '  %d. %s\n' $((i + 1)) "${CHECKPOINTS[$i]}"; done
echo

# ------------------------------------------------------------------- loop ---
declare -a TIMES=()
TOTAL_START=$(now)

for i in "${!CHECKPOINTS[@]}"; do
  n=$((i + 1))
  cp="${CHECKPOINTS[$i]}"
  echo "────────────────────────────────────────────────────────────"
  log "checkpoint $n/${#CHECKPOINTS[@]}: $cp"
  read -r -p "  Trabalhe no checkpoint e pressione ENTER para verificar (ou 'p' para pular): " ans
  [[ "$ans" == "p" ]] && { TIMES+=("pulado"); continue; }

  START=$(now)
  if verify_all; then
    TIMES+=("$(fmt_duration $(( $(now) - START ))) ✓")
  else
    TIMES+=("$(fmt_duration $(( $(now) - START ))) ✗")
    warn "verificações falharam no checkpoint $n — corrija antes de avançar"
    read -r -p "  Pressione ENTER para continuar mesmo assim, ou Ctrl+C para abortar: "
  fi
done

# ------------------------------------------------------------------ resumo --
TOTAL=$(( $(now) - TOTAL_START ))
echo "════════════════════════════════════════════════════════════"
log "resumo da slice $SLICE_NUM"
for i in "${!CHECKPOINTS[@]}"; do
  printf '  %d. %-60.60s %s\n' $((i + 1)) "${CHECKPOINTS[$i]}" "${TIMES[$i]:-—}"
done
printf '  total: %s\n\n' "$(fmt_duration "$TOTAL")"

cat <<EOF
## Retrospectiva (gerada por scripts/run-slice.sh em $(date +%F))
- Tempo total medido: $(fmt_duration "$TOTAL")
- O que a IA acertou de primeira:
- Onde precisou de correção humana:
- Ajustes feitos no AGENTS.md por causa desta slice:
- Tempo gasto (estimado) vs. sem IA:
EOF
