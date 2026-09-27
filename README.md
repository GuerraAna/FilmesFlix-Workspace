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
| 000 | Workspace setup | estrutura, AGENTS.md, contrato inicial, esqueleto do BFF | em andamento |
| 001 | Tela de detalhes do filme | só app (aprender o ciclo) | planejada |
| 002 | BFF servindo a lista de filmes | contrato + BFF + app apontando pro BFF | planejada |
| 003 | Favoritos persistidos no BFF | slice completa contrato → BFF → app | planejada |

## Rodando

```bash
# BFF
cd services/filmes-api && ./gradlew bootRun     # http://localhost:8080/v1/movies

# App
cd apps/filmes-flix && ./gradlew installDebug
```
