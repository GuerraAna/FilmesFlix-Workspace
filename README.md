# FilmesFlix Workspace

Workspace multi-repo para aprender, na prática, **desenvolvimento de ponta a ponta assistido por IA**
("vertical slices"): cada feature nasce no contrato, passa pelo BFF e termina no app Android.

> Objetivo pessoal: evoluir o uso de agentes de IA no dia a dia de engenharia, aprender BFF com
> Spring e documentar o processo como case de portfólio.

## Estrutura

```
FilmesFlix-Workspace/
├── AGENTS.md              # contexto raiz para agentes de IA (leia primeiro)
├── contracts/
│   └── openapi.yaml       # contrato único entre app e BFF
├── apps/
│   └── filmes-flix/       # submodule -> GuerraAna/FilmesFlix (Android, MVVM + Clean Arch)
├── services/
│   └── filmes-api/        # BFF em Kotlin + Spring Boot (WebFlux)
└── docs/
    ├── slices/            # specs numeradas: 000-workspace-setup, 001-tela-detalhes, ...
    └── prompts/           # templates de prompt reutilizáveis
```

## Como clonar

```bash
git clone --recurse-submodules https://github.com/GuerraAna/FilmesFlix-Workspace.git
# ou, se já clonou:
git submodule update --init --recursive
```

## Fluxo de trabalho (spec → implementação → verificação)

1. Escreva/revise a slice em `docs/slices/NNN-nome.md` (use `docs/prompts/slice-template.md`).
2. Peça ao agente cada checkpoint separadamente, nesta ordem:
   1. contrato (`contracts/openapi.yaml`)
   2. BFF (`services/filmes-api`)
   3. app (`apps/filmes-flix`)
   4. testes e correções
3. Revise cada checkpoint antes de seguir. Registre aprendizados na seção "Retrospectiva" da slice.

## Roadmap de slices

| # | Slice | Escopo | Status |
|---|---|---|---|
| 000 | Workspace setup | estrutura, AGENTS.md, contrato inicial, esqueleto do BFF | concluída |
| 001 | Tela de detalhes do filme | só app (aprender o ciclo) | concluída |
| 002 | App consome o BFF | app apontando pro BFF (lista + detalhe) | concluída |
| 003 | Favoritos persistidos no BFF | slice completa contrato → BFF → app | planejada |
| 004 | Completar setup inicial | checkboxes, retrospectivas, CI, .gitignore | concluída |
| 005 | Testes de contrato | validar DTOs do app e BFF vs OpenAPI | concluída — PR GuerraAna/FilmesFlix#6 aguardando merge |
| 006 | Coverage reports | JaCoCo + testes de ViewModel | concluída — PR GuerraAna/FilmesFlix#6 aguardando merge |
| 007 | Automatizar pipeline de slices | `scripts/run-slice.sh` com medição de tempo | concluída |

> As mudanças do app (slices 005+006) estão consolidadas em uma única PR
> (GuerraAna/FilmesFlix#6, branch `feature/contract-tests-and-coverage`) sem merge até
> revisão; depois do merge lá, o ponteiro do submodule é atualizado aqui (regra do AGENTS.md).

## Rodando

Pré-requisitos (detalhes em `AGENTS.md` → "Ambiente de desenvolvimento"):

- **JDK 17** para o BFF e **JDK 11** para o app — o `java` padrão do shell não serve para os dois.
- **Android SDK** com `platforms;android-30` + `build-tools;30.0.3`.
- **Node 20+** (para `npx @redocly/cli lint`, lint do contrato).
- Blueprints prontos em `environment.yaml` (raiz, acoplado) e `blueprints/bff-only.yaml` — o app também tem o seu em `GuerraAna/FilmesFlix`.

### Setup passo a passo (qualquer máquina)

```bash
# 1. Clone com o submodule do app
git clone --recurse-submodules https://github.com/GuerraAna/FilmesFlix-Workspace.git
cd FilmesFlix-Workspace

# 2. JDKs — macOS+brew (ajuste para seu OS; Linux: apt/yum ou https://adoptium.net)
brew install openjdk@17 openjdk@11
JDK17=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home   # Intel: /usr/local/opt/...
JDK11=/opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk/Contents/Home

# Gradle não detecta JDKs keg-only do brew sozinho — registre uma vez:
echo "org.gradle.java.installations.paths=$JDK17,$JDK11" >> ~/.gradle/gradle.properties

# 3. Android SDK — via Android Studio (SDK Manager) OU linha de comando:
#    instale cmdline-tools em ~/Library/Android/sdk (macOS) e rode:
sdkmanager "platforms;android-30" "build-tools;30.0.3"

# 4. Arquivo LOCAL do app — NÃO commitar (já está no .gitignore):
echo "sdk.dir=$HOME/Library/Android/sdk" > apps/filmes-flix/local.properties
```

### Arquivos sensíveis/locais — nunca no repositório

Estes arquivos contêm caminhos da sua máquina ou segredos e **ficam apenas localmente**
(todos já cobertos pelo `.gitignore` da raiz e do app):

| Arquivo | Conteúdo sensível | Como criar |
|---|---|---|
| `apps/filmes-flix/local.properties` | `sdk.dir=/Users/<voce>/...` | `echo "sdk.dir=<seu SDK>" > apps/filmes-flix/local.properties` |
| `~/.gradle/gradle.properties` | caminhos de JDK da sua máquina | comando do passo 2 acima |
| `.env`, `*.pem`, `*.key`, `*.keystore` | segredos/credenciais | não existem ainda; se criar, ficam locais |

### Executando

```bash
# Só BFF
cd services/filmes-api && JAVA_HOME=$JDK17 ./gradlew bootRun     # http://localhost:8080/v1/movies

# Só app (testes/build; rodar no emulador exige o BFF no ar)
cd apps/filmes-flix && JAVA_HOME=$JDK11 ./gradlew testDebugUnitTest

# Acoplado: BFF no ar + emulador Android 11+ aberto
cd apps/filmes-flix && JAVA_HOME=$JDK11 ./gradlew installDebug   # app chama http://10.0.2.2:8080/
```

### Verificação completa (CI local)

```bash
# Tudo, com medição de tempo por checkpoint:
./scripts/run-slice.sh <NNN>

# Ou manualmente:
cd services/filmes-api && JAVA_HOME=$JDK17 ./gradlew test jacocoTestCoverageVerification
cd apps/filmes-flix  && JAVA_HOME=$JDK11 ./gradlew testDebugUnitTest jacocoTestReport
npx --yes @redocly/cli lint contracts/openapi.yaml
```

Reports de coverage: `services/filmes-api/build/reports/jacoco/` e
`apps/filmes-flix/app/build/reports/jacoco/`.
