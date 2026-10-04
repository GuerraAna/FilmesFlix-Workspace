# Slice 000: Workspace setup

## Contexto
Criar a base para praticar slices E2E com agentes de IA usando o `FilmesFlix` (Android, MVVM + Clean
Architecture) como front e um BFF próprio em Spring como backend. Decisões desta slice valem para todas as seguintes.

## Decisões
| Decisão | Escolha | Motivo |
|---|---|---|
| Como vincular o app | git submodule em `apps/filmes-flix` | mantém histórico/PRs no repo original; o workspace só aponta versão |
| Stack do BFF | Kotlin + Spring Boot 3 (WebFlux) | Kotlin já é conhecido; Spring é a stack de mercado que quero aprender |
| Contrato | OpenAPI 3.1 em `contracts/openapi.yaml` | fonte única; permite gerar DTOs e validar consumidores |
| Fonte de dados do BFF | mock JSON local primeiro, TMDB depois | evita depender de chave de API para começar |
| Ordem de trabalho | contrato → BFF → app → testes | força pensar na interface antes da implementação |

## Checkpoints
1. [x] Estrutura de pastas, `AGENTS.md`, `README.md`
2. [x] Submodule `apps/filmes-flix`
3. [x] `contracts/openapi.yaml` com `GET /v1/movies` e `GET /v1/movies/{id}`
4. [x] Esqueleto do BFF em `services/filmes-api` respondendo `GET /v1/movies` a partir do mock
5. [x] Configurar CI (GitHub Actions) rodando `./gradlew test` do BFF e lint do contrato

## Critérios de aceite
- [x] `git clone --recurse-submodules` traz o app
- [x] `cd services/filmes-api && ./gradlew test` passa
- [x] `curl localhost:8080/v1/movies` retorna a lista do mock

## Fora de escopo
- Alterar qualquer código do app (isso começa na slice 001)
- Integração real com TMDB

## Retrospectiva
- O que a IA acertou de primeira: estrutura de pastas, AGENTS.md completo (incluindo a tabela
  de JVMs por módulo e os comandos de verificação), contrato OpenAPI inicial, esqueleto do BFF
  com testes `@WebFluxTest` e unitário do service já passando.
- Onde precisou de correção humana: o contrato inicial falhou no lint do redocly na primeira
  execução da CI — faltava `security: []` explícito e uma resposta 4xx em `GET /v1/movies`
  (corrigido em `fbf4336`, PR #1). A CI pegou o problema antes do merge, validando a decisão
  de configurá-la nesta slice.
- Ajustes feitos no AGENTS.md por causa desta slice: nenhum — as regras iniciais se mantiveram.
- Tempo gasto (estimado) vs. sem IA: ~1h com IA vs. ~4h estimadas sem IA (o boilerplate de
  Gradle Kotlin DSL + WebFlux + testes teria consumido a maior parte do tempo).
