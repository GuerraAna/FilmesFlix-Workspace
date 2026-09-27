# Slice NNN: <nome curto>

## Contexto
<Por que essa feature existe. 2–3 linhas. Link para issue/README se houver.>

## Contrato
- Endpoint(s): `<MÉTODO> /v1/...` (ver `contracts/openapi.yaml`)
- Schemas novos/alterados: `<Nome>`
- Consumidores afetados: `apps/filmes-flix` (`<arquivo>`), `services/filmes-api` (`<arquivo>`)

## Checkpoints (um commit/PR cada, nesta ordem)
1. **Contrato** — alterar `openapi.yaml`; rodar lint.
2. **BFF** — `<Controller>`, `<Service>`, `<Client>`; teste `@WebFluxTest` + unitário.
3. **App** — `data/remote/<Dto>`, `data/mapper`, `domain/usecase/<UseCase>`, `presentation/<Screen>`; teste da ViewModel.
4. **Verificação** — rodar todos os comandos de `AGENTS.md`; corrigir falhas.

## Critérios de aceite
- [ ] <comportamento observável 1>
- [ ] <comportamento observável 2>
- [ ] Testes passando no BFF e no app

## Fora de escopo
- <o que NÃO fazer nesta slice>

## Prompt inicial para o agente
```
Leia AGENTS.md e docs/slices/NNN-<nome>.md. Execute apenas o checkpoint 1 (contrato)
e pare para revisão. Não avance para o BFF nem para o app.
```

## Retrospectiva (preencher ao final)
- O que a IA acertou de primeira:
- Onde precisou de correção humana:
- Ajustes feitos no AGENTS.md por causa desta slice:
- Tempo gasto (estimado) vs. sem IA:
