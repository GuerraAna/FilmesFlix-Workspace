# Slice 002: App consome o BFF

## Contexto
Até a slice 001 o app chama o JSON legado hospedado no GitHub do autor original
(`raw.githubusercontent.com/natanfelipe/FilmesFlixJson`). Esta slice troca a fonte de dados pelo
BFF próprio (`services/filmes-api`), que expõe o contrato em inglês definido em
`contracts/openapi.yaml`. É a primeira slice que atravessa contrato → BFF → app de ponta a ponta.

## Contrato
- Endpoints: `GET /v1/movies` (`listMovies` → `MovieSummary`), `GET /v1/movies/{id}`
  (`getMovieById` → `MovieDetail`) — ver `contracts/openapi.yaml`.
- Nenhuma alteração no `openapi.yaml`: o contrato atual já cobre lista e detalhe.
- Consumidores afetados: `apps/filmes-flix` (`data/remote`, `data/mapper`, `data/repository`,
  `domain/repository`, `domain/usecase`, `presentation/detail`, `AndroidManifest`).

## Decisões
- `MovieSummary` não traz `overview`/`releaseDate`: a lista renderiza poster + título e a tela
  de detalhes busca `GET /v1/movies/{id}` para descrição e data.
- `BASE_URL` do app: `http://10.0.2.2:8080/` (loopback do emulador para o host). Como o BFF
  local não tem TLS, o manifest passa a usar `usesCleartextTraffic="true"`.
- O domínio do app mantém `Movie` em português (legado); `data/mapper` traduz os DTOs em inglês.
- O BFF devolve `releaseDate` em ISO (`yyyy-MM-dd`) — o `DateFormatter` passa a parsear ISO e
  exibir `dd/MM/yyyy`.

## Checkpoints (um commit/PR cada, nesta ordem)
1. **Contrato** — sem alteração; lint já verde (workspace PR #1).
2. **BFF** — já implementado na slice 000; verificar `./gradlew test` e `curl` dos dois endpoints.
3. **App** — `data/remote/MovieDtos`, `data/mapper/MovieMapper`, `MovieApi` com os dois endpoints,
   `MovieRepositoryImpl`, `GetMovieByIdUseCase`, `DetailViewModel` + factory, `DetailActivity`
   observando `UiState`, manifest com cleartext; testes: `MovieMapperTest`, `DateFormatterTest`
   atualizado para ISO.
4. **Verificação** — rodar todos os comandos de `AGENTS.md`; corrigir falhas.

## Critérios de aceite
- [ ] Com o BFF no ar (`localhost:8080`), a lista do app mostra os filmes do mock do BFF
- [ ] Tocar em um filme abre detalhes com descrição e data vindas de `GET /v1/movies/{id}`
- [ ] Falha de rede na lista gera `UiState.Error` (Toast); no detalhe, descrição/data caem no
  texto padrão
- [ ] `./gradlew testDebugUnitTest` (app), `./gradlew test` (BFF) e `redocly lint` passam

## Fora de escopo
- Integração real com TMDB (BFF continua servindo o mock local)
- Paginação, cache e retry
- `BASE_URL` configurável por build type ou apontar para IP de dispositivo físico

## Prompt inicial para o agente
```
Leia AGENTS.md e docs/slices/002-consumir-bff.md. O contrato já está fechado e o BFF já responde
os dois endpoints: comece pelo checkpoint 3 (app) em uma branch feature/002-consumir-bff criada
a partir de codebase_inicial. Rode ./gradlew testDebugUnitTest ao final.
```

## Retrospectiva (preencher ao final)
- O que a IA acertou de primeira:
- Onde precisou de correção humana:
- Ajustes feitos no AGENTS.md por causa desta slice:
- Tempo gasto (estimado) vs. sem IA:
