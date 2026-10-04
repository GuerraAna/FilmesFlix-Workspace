# Slice 004: Completar setup inicial (checkboxes, retrospectiva, CI)

## Contexto
A slice 000 estruturou o workspace, mas os critérios de aceite não foram marcados como concluídos e a retrospectiva ficou vazia. Além disso, o workflow do GitHub Actions mencionado não está evidenciado no repositório. Esta slice finaliza o setup inicial de forma auditável.

## Contrato
- Nenhuma alteração no `openapi.yaml` — slice de infraestrutura.

## Checkpoints (um commit/PR cada, nesta ordem)
1. **Verificar CI existente** — conferir se `.github/workflows/` existe e está funcionando; se não, criar workflow básico rodando testes do BFF e lint do contrato.
2. **Marcar critérios de aceite da 000** — atualizar `docs/slices/000-workspace-setup.md` marcando os checkboxes como concluídos.
3. **Preencher retrospectiva da 000** — documentar decisões, problemas encontrados e tempo gasto.
4. **Preencher retrospectivas da 001 e 002** — baseado no histórico do repo (commits, branches), inferir o que funcionou e o que precisou de correção.
5. **Atualizar roadmap do README** — marcar slices 000, 001, 002 como concluídas com links para commits de merge.

## Critérios de aceite
- [x] Workflow do GitHub Actions existe e passa (estendido com job do app + artifacts de coverage)
- [x] `docs/slices/000-workspace-setup.md` tem checkboxes marcados e retrospectiva preenchida
- [x] `docs/slices/001-tela-detalhes.md` tem retrospectiva preenchida
- [x] `docs/slices/002-consumir-bff.md` tem retrospectiva preenchida
- [x] `README.md` tem roadmap atualizado com status das slices
- [x] `.gitignore` da raiz cobre arquivos sensíveis (`local.properties`, `.env`, chaves)

## Fora de escopo
- Alterar funcionalidade do app ou BFF
- Mudar a arquitetura

## Prompt inicial para o agente
```
Leia AGENTS.md e docs/slices/004-completar-setup-inicial.md. Execute o checkpoint 1
(verificar CI existente) e pare para revisão. Se o workflow não existir, crie um workflow
básico em .github/workflows/ci.yml que rode testes do BFF e lint do contrato.
```

## Retrospectiva
- O que a IA acertou de primeira: a CI já existia e foi estendida (job do app com Android SDK +
  upload de reports JaCoCo como artifacts); retrospectivas reconstruídas a partir do histórico
  real de commits/PRs; `.gitignore` reforçado cobrindo `local.properties`, `.env` e chaves.
- Onde precisou de correção humana: decisão do dono sobre como tratar o submodule
  (branch + PR sem merge) em vez de assumir autonomia.
- Ajustes feitos no AGENTS.md por causa desta slice: nenhum — a mudança foi no `.gitignore`.
- Tempo gasto (estimado) vs. sem IA: ~20min com IA vs. ~1h sem IA.
