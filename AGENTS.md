# AGENTS.md — FilmesFlix Workspace

Este repositório é um **workspace multi-repo** para praticar desenvolvimento assistido por IA em
"vertical slices" (fatias completas de funcionalidade que atravessam contrato → BFF → app).
Leia este arquivo inteiro antes de qualquer alteração.

## Visão geral do workspace

| Caminho | O que é | Stack |
|---|---|---|
| `apps/filmes-flix/` | App Android (git submodule de `GuerraAna/FilmesFlix`) | Kotlin, MVVM, Clean Architecture, Jetpack Compose (M3), Hilt, Room, Retrofit, Coroutines/Flow |
| `services/filmes-api/` | BFF próprio que serve o app | Kotlin + Spring Boot 3 (WebFlux), Gradle Kotlin DSL |
| `contracts/openapi.yaml` | **Fonte da verdade** do contrato entre app e BFF | OpenAPI 3.1 |
| `docs/slices/` | Slice specs (uma feature = um arquivo numerado) | Markdown |
| `docs/prompts/` | Prompts/templates reutilizáveis para agentes | Markdown |

## Como uma feature atravessa o sistema

1. **Contrato** — a feature começa em `contracts/openapi.yaml`. Nada é implementado sem o contrato atualizado.
2. **BFF** — `services/filmes-api` implementa o endpoint conforme o contrato
   (`controller` → `service` → `client`/`repository`). O BFF agrega/adapta a API pública de filmes
   (`https://api.themoviedb.org/3` ou o mock em `services/filmes-api/src/main/resources/mock/`).
3. **App** — `apps/filmes-flix` consome o BFF via Retrofit (`data/remote`), mapeia para `domain/model`
   e expõe pela `ViewModel` (`presentation`).
4. **Testes** — unitário no `service` do BFF, unitário na `ViewModel`, e um teste de contrato garantindo
   que os DTOs do app batem com o `openapi.yaml`.

## Regras para agentes

- **Contrato primeiro.** Antes de mudar um endpoint, edite `contracts/openapi.yaml` e liste todos os
  consumidores afetados no workspace (`grep -rn "<path>" apps services`).
- **Trabalhe por slice.** Toda tarefa deve referenciar um arquivo em `docs/slices/NNN-*.md`. Se não existir,
  crie a spec primeiro usando `docs/prompts/slice-template.md` e pare para revisão.
- **Checkpoints, não big-bang.** Ordem obrigatória: contrato → BFF → app → testes. Abra um commit
  (ou PR) por checkpoint; cada um deve compilar sozinho.
- **Submodule é read-only a partir daqui.** Alterações no app são feitas dentro de `apps/filmes-flix`
  em branch própria do repo `GuerraAna/FilmesFlix` e o ponteiro do submodule é atualizado neste repo
  depois do merge lá.
- **Não invente API externa.** Se a API de filmes não tiver o dado, use o mock e registre a limitação na slice.
- **Comandos de verificação** (rodar antes de finalizar):
  - BFF: `cd services/filmes-api && JAVA_HOME=$JDK17 ./gradlew test jacocoTestCoverageVerification`
  - App: `cd apps/filmes-flix && JAVA_HOME=$JDK17 ./gradlew testDebugUnitTest jacocoTestReport`
  - Contrato: `npx @redocly/cli lint contracts/openapi.yaml`
  - Tudo de uma vez (com medição de tempo por checkpoint): `./scripts/run-slice.sh NNN`
- **Testes de contrato.** `ContractTest` (BFF: `api/ContractTest.kt`; app: `data/remote/ContractTest.kt`)
  parseia `contracts/openapi.yaml` e valida os DTOs. No app, o teste é pulado via JUnit
  assumption quando o clone é standalone (o contrato só existe dentro do workspace).
- **Coverage.** JaCoCo nos dois módulos com `jacocoTestCoverageVerification`: BFF ≥ 70% linhas
  (geral) e ≥ 80% em `domain`; app ≥ 50% geral e ≥ 80% em `domain.*`. Reports HTML em
  `*/build/reports/jacoco/`. Excluídos da medição: classes geradas, Activities/Adapters e
  plumbing de rede (API client/repository impl).

- **Commits** seguem Conventional Commits com escopo: `feat(bff): ...`, `feat(app): ...`, `docs(slice): ...`,
  `chore(contract): ...`.
- **Idioma:** documentação e specs em português; código, nomes de classes e mensagens de commit em inglês.

## Ambiente de desenvolvimento

Cada módulo exige uma JVM diferente — não assuma que o `java` padrão do shell serve:

| Módulo | Ferramenta | JVM exigida | Por quê |
|---|---|---|---|
| `services/filmes-api` | Gradle 8.12, Spring Boot 3 | **JDK 17** (toolchain) | `build.gradle.kts` fixa `languageVersion = 17` |
| `apps/filmes-flix` | Gradle 8.10, AGP 8.6, Kotlin 2.0 | **JDK 17** | AGP 8 exige JDK 17 para rodar |
| `contracts/` | redocly via `npx` | Node 20+ | lint do OpenAPI |

Setup macOS (brew, sem sudo):

```bash
brew install openjdk@17
JDK17=/opt/homebrew/opt/openjdk@17   # ajuste se não for Apple Silicon
# Gradle não detecta JDKs keg-only do brew sozinho:
echo "org.gradle.java.installations.paths=$JDK17/libexec/openjdk.jdk/Contents/Home" \
  >> ~/.gradle/gradle.properties
```

App Android:

- SDK precisa de `platforms;android-35` e `build-tools;35.0.0` (`compileSdk 35` no `app/build.gradle.kts`).
- Crie `apps/filmes-flix/local.properties` com `sdk.dir=<caminho do Android SDK>` (arquivo gitignored).
- O app chama o BFF em `http://10.0.2.2:8080/` (`BASE_URL` em `di/AppModule.kt`) — loopback do
  **emulador** para o host. Em aparelho físico, troque pelo IP da máquina na rede.
- `usesCleartextTraffic="true"` está liberado no manifest porque o BFF local é HTTP.

Modos de execução (detalhes nos blueprints `environment.yaml` na raiz e em `blueprints/`):

- **Só BFF**: `cd services/filmes-api && JAVA_HOME=$JDK17 ./gradlew bootRun` → `curl localhost:8080/v1/movies`
- **Só app**: `cd apps/filmes-flix && JAVA_HOME=$JDK17 ./gradlew testDebugUnitTest` (compila/testa sem backend; rodar no emulador exige o BFF no ar — sem rede, a lista cai no cache do Room)
- **Acoplado**: suba o BFF (`bootRun`), abra um emulador Android 11+ e `JAVA_HOME=$JDK17 ./gradlew installDebug` no app.

## Convenções do app (`apps/filmes-flix`)

- Pacote raiz: `com.br.natanfc.filmesflix`.
- Camadas alvo: `data/` (remote/dto, local/Room, mapper, repository impl), `domain/` (model,
  repository interface, usecase `observe*`/`refresh*`), `presentation/` (screens Compose,
  viewmodel, `ui/theme`, `navigation`).
- UI em **Jetpack Compose** (Material 3 + Navigation Compose). XML só para recursos
  (strings, tema plataforma do manifest). Não recriar layouts/adapters/ViewBinding.
- Estado de tela via `StateFlow<UiState>` (Loading / Success / Error) coletado com
  `collectAsStateWithLifecycle`. Nunca expor `Movie` cru na View.
- DI via **Hilt**: `@HiltViewModel` + `@Inject`; args de navegação via `SavedStateHandle`.
- **Offline-first**: Room é a fonte de verdade (`Flow` do DAO); a rede só alimenta o cache
  via `refresh*`. Payloads de summary têm `upsert` parcial para não apagar campos de detail.
- Model atual: `Movie(id, title, posterUrl, overview, releaseDate)` — nomes em inglês,
  alinhados ao contrato; o app mapeia DTO → Entity → Domain via `data/mapper`.

## CI: submodule privado

`apps/filmes-flix` é um **repositório privado** (`GuerraAna/FilmesFlix`). O `GITHUB_TOKEN` do
Actions só cobre o workspace — sem secret extra, o checkout do submodule falha com
`Repository not found` (GitHub devolve 404, não 403, para não vazar a existência do repo).

Para o job `app` funcionar, o workflow usa `secrets.APP_SUBMODULE_TOKEN`: um fine-grained
PAT com `Contents: Read` em **dois** repositórios — `FilmesFlix` e `FilmesFlix-Workspace`
(o token do checkout também clona o repo principal). Se o secret não existir, o job `app`
falha no checkout; BFF e contrato não dependem dele.

## Convenções do BFF (`services/filmes-api`)

- Pacote raiz: `com.guerraana.filmesapi`.
- Estrutura: `api/` (controllers + DTOs gerados do contrato), `domain/` (modelos + services),
  `infra/` (clients HTTP, mocks, config).
- Erros seguem RFC 9457 (`application/problem+json`).
- Toda rota nova tem teste `@WebFluxTest` do controller e teste unitário do service.

## Onde encontrar mais contexto

- `README.md` — objetivo do workspace e roadmap de slices.
- `docs/slices/000-workspace-setup.md` — decisões iniciais e o "porquê" desta estrutura.
- `docs/prompts/` — templates de prompt para spec, implementação e revisão.
