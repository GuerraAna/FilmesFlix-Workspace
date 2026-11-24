# Slice 008: Modernização do app (Compose + Flow + Hilt + Room)

## Contexto
O app foi mantido na stack original do bootcamp (AGP 4.1, Kotlin 1.4, XML/LiveData, Executor
bloqueante, DI manual). Para o workspace servir de referência de mercado, o app precisa refletir
a stack atual exigida em vagas Android: Jetpack Compose, Coroutines/Flow, Hilt, Room
(offline-first) e toolchain moderna (Kotlin 2.x, AGP 8.x, JDK 17, Kotlin DSL + version catalog).

## Contrato
- Nenhuma alteração no `openapi.yaml` — modernização interna do app.
- DTOs do app continuam fiéis aos schemas `MovieSummary`/`MovieDetail` (validado por `ContractTest`).

## Checkpoints (um commit cada, nesta ordem)
1. **Toolchain** — Gradle 8.10.2, AGP 8.6.1, Kotlin 2.0.21, JDK 17, compileSdk/targetSdk 35,
   minSdk 24, Groovy → Kotlin DSL, `gradle/libs.versions.toml`, `namespace` no build file.
2. **Data/Domain** — Retrofit `suspend` + kotlinx.serialization + OkHttp logging, Room como
   cache local (offline-first: Flow do DAO como fonte de verdade + refresh da rede), Hilt
   para DI (fim das factories manuais), `Movie` com campos em inglês, `FilmesFlixApp`.
3. **Presentation** — UI em Compose (Material 3): `MainActivity` única com `NavHost`,
   `MovieListScreen`, `MovieDetailScreen`, ViewModels com `StateFlow` + `viewModelScope`.
   Remover XML layouts, adapters, ViewBinding, `DetailActivity`.
4. **Testes** — ViewModels com `kotlinx-coroutines-test` + Turbine, `ContractTest` migrado
   para kotlinx.serialization, `DateFormatter` em `java.time`, JaCoCo atualizado para
   caminhos do AGP 8 e exclusões de código gerado (Hilt/Room/Compose).
5. **Workspace** — AGENTS.md (JDK 17 no app, novas convenções), CI do job `app`
   (Java 17, android-35), `scripts/run-slice.sh`, README.

## Critérios de aceite
- [ ] `cd apps/filmes-flix && JAVA_HOME=$JDK17 ./gradlew testDebugUnitTest jacocoTestReport` passa
- [ ] Coverage ≥50% geral / ≥80% `domain.*` mantido
- [ ] App lista filmes e abre detalhe via navegação Compose (mesmas rotas do contrato)
- [ ] Cache Room: segunda abertura exibe dados sem rede
- [ ] BFF e contrato intocados (`redocly lint` e testes do BFF continuam passando)

## Fora de escopo
- Novos endpoints ou features de negócio
- Modularização multi-módulo (app continua single-module)
- Publicação no Play Store

## Retrospectiva (preencher ao final)
- O que a IA acertou de primeira:
- Onde precisou de correção humana:
- Ajustes feitos no AGENTS.md por causa desta slice:
- Tempo gasto (estimado) vs. sem IA:
