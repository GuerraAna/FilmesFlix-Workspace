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
- [ ] `git clone --recurse-submodules` traz o app
- [ ] `cd services/filmes-api && ./gradlew test` passa
- [ ] `curl localhost:8080/v1/movies` retorna a lista do mock

## Fora de escopo
- Alterar qualquer código do app (isso começa na slice 001)
- Integração real com TMDB

## Retrospectiva
- (preencher após concluir o checkpoint 5)
