# Slice 005: Testes de contrato (DTOs do app vs OpenAPI)

## Contexto
O AGENTS.md menciona "teste de contrato garantindo que DTOs do app batem com openapi.yaml", mas isso não está implementado. Contract-first precisa de validação automatizada para evitar drift entre contrato e implementação. Esta slice adiciona testes que validam se os DTOs do app correspondem aos schemas do OpenAPI.

## Contrato
- Nenhuma alteração no `openapi.yaml` — slice de infraestrutura de teste.
- Schemas existentes: `MovieSummary`, `MovieDetail`, `Problem`.

## Checkpoints (um commit/PR cada, nesta ordem)
1. **Pesquisar ferramenta de validação** — investigar opções para Kotlin/Android (ex: `openapi-generator`, manual reflection, biblioteca de validação). Parar para decisão do humano.
2. **Implementar validação no BFF** — criar teste em `services/filmes-api` que valida DTOs de resposta (`MovieSummaryResponse`, `MovieDetailResponse`) contra schemas do OpenAPI usando a ferramenta escolhida.
3. **Implementar validação no app** — criar teste em `apps/filmes-flix` que valida DTOs do app (`Movie`, `MovieDetail` se existir) contra schemas do OpenAPI. Pode requerer cópia do `openapi.yaml` para o projeto do app.
4. **Integrar no pipeline** — adicionar testes de contrato aos comandos de verificação no AGENTS.md e no CI.
5. **Documentar** — atualizar AGENTS.md com instruções de como rodar os testes de contrato isoladamente.

## Critérios de aceite
- [x] Teste no BFF valida que `MovieSummaryResponse`/`MovieDetailResponse` batem com os schemas do OpenAPI (`api/ContractTest.kt`, resolve `allOf`, checa required/tipos/formatos)
- [x] Teste no app valida que `MovieSummaryDto`/`MovieDetailDto` batem com os schemas + round-trip Gson (`data/remote/ContractTest.kt`)
- [x] Testes falham se o OpenAPI ou os DTOs forem alterados de forma inconsistente (campo não declarado → `fail`; required ausente → `assertTrue` falha)
- [x] Testes passam: BFF `./gradlew test` (12 testes), app `./gradlew testDebugUnitTest` (3 contrato + demais)
- [x] AGENTS.md atualizado (seção "Testes de contrato" nos comandos de verificação)
- [x] PR aberta no app: GuerraAna/FilmesFlix#6 (branch `feature/contract-tests-and-coverage`, sem merge — aguardando revisão)

## Fora de escopo
- Alterar funcionalidade do app ou BFF
- Validação em runtime (somente em testes)

## Prompt inicial para o agente
```
Leia AGENTS.md e docs/slices/005-testes-contrato.md. Execute o checkpoint 1 (pesquisar
ferramenta de validação) e pare para revisão. Pesquise bibliotecas Kotlin/Android que
permitam validar DTOs contra schemas OpenAPI. Considere: openapi-generator, spek, ou
validação manual via reflection. Apresente 2-3 opções com prós/contras.
```

## Retrospectiva
- O que a IA acertou de primeira: abordagem pragmática sem geradores — parsear o YAML e validar
  DTO serializado contra schema (required, tipos, `format: date`/`uri`), com resolução de
  `allOf`/`$ref`. Escolha de libs mínimas: Jackson YAML no BFF (já no BOM do Spring),
  SnakeYAML no app (Gson, sem Jackson no classpath).
- Onde precisou de correção humana: duas pegadinhas de plataforma — `LocalDate` serializa como
  array sem `WRITE_DATES_AS_TIMESTAMPS` desabilitado (o Spring faz isso em produção, o teste
  precisou replicar); e no app, JUnit4 `fail()` retorna `void`/`Unit` (não `Nothing`), então
  `?: fail(...)` tornava a variável nullable — trocado por `error()` do Kotlin.
- Ajustes feitos no AGENTS.md por causa desta slice: nova seção "Testes de contrato"
  documentando onde ficam e o comportamento de skip standalone do app.
- Tempo gasto (estimado) vs. sem IA: ~1h com IA vs. ~3h sem IA (pesquisar/abandonar
  openapi-generator por peso, desenhar o validador, duas iterações de depuração).
