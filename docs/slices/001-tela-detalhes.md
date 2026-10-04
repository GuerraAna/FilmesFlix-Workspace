# Slice 001: Tela de detalhes do filme (somente app)

## Contexto
O README do FilmesFlix propõe como desafio uma tela de detalhes com as informações que já vêm na
requisição (`descricao`, `data_lancamento`). É a primeira slice executada por IA: o objetivo é aprender o
ciclo spec → implementação → revisão **sem** envolver o BFF ainda.

## Contrato
- Nenhuma alteração no `openapi.yaml` — o app continua consumindo a API atual.
- `Movie` já traz `descricao` e `dataLancamento` (ver `apps/filmes-flix/.../model/Movie.kt`).

## Checkpoints
1. **Refatoração mínima** — mover `Movie` para `domain/model`, criar `presentation/` e aplicar ViewBinding
   na `MainActivity` (removendo `kotlin-android-extensions`). Sem mudança de comportamento.
2. **Navegação** — clique em um item do `MoviesAdapter` abre `DetailActivity` passando o `Movie`
   (`Parcelable` já existe).
3. **Tela** — `activity_detail.xml` com poster grande (Coil), título, data de lançamento formatada
   (`dd/MM/yyyy`) e descrição; tratar `descricao == null` com texto padrão.
4. **Testes** — unitário para o formatador de data e para o mapeamento `Movie -> DetailUiModel`.

## Critérios de aceite
- [x] Tocar em um filme na lista abre a tela de detalhes com poster, título, data e descrição
- [x] Filmes sem descrição mostram "Descrição indisponível"
- [x] Botão voltar retorna para a lista sem recarregar a API
- [x] `./gradlew testDebugUnitTest` passa

## Fora de escopo
- Migrar para Compose
- Chamar o BFF (slice 002)
- Favoritar (slice 003)

## Prompt inicial para o agente
```
Leia AGENTS.md e docs/slices/001-tela-detalhes.md. Trabalhe dentro de apps/filmes-flix em uma
branch feature/001-detail-screen. Execute apenas o checkpoint 1 (refatoração mínima) e pare para
revisão. Não implemente a tela ainda.
```

## Retrospectiva
- O que a IA acertou de primeira: cada checkpoint virou um commit limpo no app
  (`073b3d8` refactor, `4772670` navegação, `6813e10` tela, `75880dc` testes), a remoção de
  `kotlin-android-extensions` foi feita sem quebrar telas existentes, e os testes de
  `DateFormatter` + `Movie -> DetailUiModel` nasceram junto com a implementação.
- Onde precisou de correção humana: revisão de detalhes visuais no layout (dimens/estilo)
  e validação manual no emulador — o comportamento "voltar sem recarregar" só pôde ser
  conferido rodando o app, não por teste unitário.
- Ajustes feitos no AGENTS.md por causa desta slice: registro explícito de que novas telas
  usam ViewBinding (synthetics deprecado) e que a View nunca recebe `Movie` cru — passa por
  `UiState`/UiModel.
- Tempo gasto (estimado) vs. sem IA: ~1h30 com IA vs. ~4h estimadas sem IA (a migração de
  synthetics para ViewBinding e a organização em camadas seriam os maiores consumidores).
