# Slice 006: Coverage reports (JaCoCo no BFF e app)

## Contexto
O projeto não tem métricas de coverage visíveis. Em um workspace que prega qualidade e testes em todos os layers, coverage é uma métrica importante para garantir que testes estão cobrindo o código crítico. Esta slice adiciona JaCoCo para gerar reports de cobertura nos dois módulos.

## Contrato
- Nenhuma alteração no `openapi.yaml` — slice de infraestrutura de teste.

## Checkpoints (um commit/PR cada, nesta ordem)
1. **Configurar JaCoCo no BFF** — adicionar plugin JaCoCo ao `build.gradle.kts` do `services/filmes-api`, configurar thresholds mínimos (ex: 70% overall, 80% em domain) e gerar report HTML/XML.
2. **Configurar JaCoCo no app** — adicionar plugin JaCoCo ao `app/build.gradle` de `apps/filmes-flix`, configurar thresholds e gerar report. Verificar compatibilidade com Android (pode requerer `android-junit5` ou configuração específica).
3. **Integrar no pipeline** — adicionar comando de coverage ao AGENTS.md e ao CI. O comando deve falhar se thresholds não forem atingidos.
4. **Excluir código não crítico** — configurar excludes para DTOs, modelos de dados, classes de View, etc. Focar coverage em services, usecases, controllers.
5. **Documentar** — atualizar AGENTS.md com instruções de como rodar coverage isoladamente e como interpretar os reports.

## Critérios de aceite
- [x] `cd services/filmes-api && ./gradlew jacocoTestReport` gera report em `build/reports/jacoco/` (100% linhas nas classes medidas)
- [x] `cd apps/filmes-flix && ./gradlew jacocoTestReport` gera report em `app/build/reports/jacoco/` (~89% nas classes medidas)
- [x] Thresholds: BFF ≥70% geral / ≥80% `domain`; app ≥50% geral / ≥80% `domain.*` — verification falha se não atingir
- [x] Comando de verificação no AGENTS.md inclui coverage
- [x] CI roda `jacocoTestCoverageVerification` (BFF) e `jacocoTestReport` (app) + upload de artifacts
- [x] Excludes: app main class e DTOs no BFF; geradas/UI/network no app — ViewModels NÃO excluídos (testes adicionados)

## Fora de escopo
- Aumentar coverage apenas para bater thresholds (adicionar testes vazios não é aceitável)
- Alterar funcionalidade do app ou BFF

## Prompt inicial para o agente
```
Leia AGENTS.md e docs/slices/006-coverage-reports.md. Execute o checkpoint 1 (configurar
JaCoCo no BFF) e pare para revisão. Adicione o plugin JaCoCo ao build.gradle.kts do BFF,
configure thresholds mínimos (70% overall, 80% em domain) e verifique se o report é gerado
corretamente com ./gradlew jacocoTestReport.
```

## Retrospectiva
- O que a IA acertou de primeira: em vez de só excluir código não testado, detectou que os
  ViewModels do app não tinham testes (o AGENTS.md exige) e os criou —
  `MainViewModelTest`/`DetailViewModelTest` com `InstantTaskExecutorRule` + `CountDownLatch`
  sobre o executor interno; e criou teste para `MockMovieRepository` em vez de excluí-lo.
  Coverage real: BFF 100% linhas medidas, app ~89%.
- Onde precisou de correção humana: nenhuma relevante; a escolha de não excluir ViewModels foi
  deliberada e revisada.
- Ajustes feitos no AGENTS.md por causa desta slice: seção "Coverage" com thresholds,
  caminhos dos reports e política de excludes.
- Tempo gasto (estimado) vs. sem IA: ~45min com IA vs. ~2h sem IA (configurar JaCoCo no
  AGP 4.1.1 — paths de classes/exec do toolchain antigo — é a parte que mais consome).
