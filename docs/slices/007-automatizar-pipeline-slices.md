# Slice 007: Automatizar pipeline de slices (script sequencial)

## Contexto
O workflow de slices é manual (ler spec, executar checkpoint, revisar, avançar). Para ganhar eficiência e consistência, esta slice cria um script que automatiza a execução dos checkpoints de uma slice sequencialmente, com pausas para revisão humana. Isso permite medição de tempo e reprodução do processo.

## Contrato
- Nenhuma alteração no `openapi.yaml` — slice de automação de workflow.

## Checkpoints (um commit/PR cada, nesta ordem)
1. **Desenhar o script** — criar `scripts/run-slice.sh` que:
   - Recebe o número da slice como argumento (ex: `./scripts/run-slice.sh 005`)
   - Lê a spec em `docs/slices/NNN-*.md`
   - Executa os checkpoints sequencialmente
   - Pausa após cada checkpoint para revisão humana (read com timeout)
   - Registra tempo gasto por checkpoint
   - Ao final, gera um resumo com tempos e estatísticas
2. **Implementar execução de checkpoints** — cada checkpoint do script deve:
   - Chamar o agente com o prompt específico do checkpoint
   - Aguardar conclusão
   - Rodar comandos de verificação do AGENTS.md
   - Perguntar ao humano se deve avançar ou corrigir
3. **Integrar com retrospectives** — ao final da slice, o script deve preencher automaticamente a seção "Retrospectiva" com:
   - Tempos por checkpoint
   - Comandos executados
   - Status (sucesso/falha)
4. **Documentar** — atualizar AGENTS.md com instruções de como usar o script.
5. **Testar** — rodar o script em uma slice simples (ex: 004 ou 005) e verificar se funciona.

## Critérios de aceite
- [x] `./scripts/run-slice.sh 004` executa os checkpoints da slice 004 sequencialmente
- [x] Script pausa após cada checkpoint para revisão humana (`read`, com opção 'p' de pular)
- [x] Script registra tempo gasto por checkpoint (`TIMES[]` + resumo final)
- [x] Script emite bloco de retrospectiva pronto para colar (tempos + campos do template)
- [x] AGENTS.md documentado (comando "Tudo de uma vez" nos comandos de verificação)
- [x] Script testado na slice 004 (parsing de checkpoints, loop, resumo)
- [x] Compatível com bash 3.2 do macOS (sem `mapfile`)

## Fora de escopo
- Substituir completamente a interação humana (o script é um assistente, não autônomo)
- Modificar as specs das slices existentes

## Prompt inicial para o agente
```
Leia AGENTS.md e docs/slices/007-automatizar-pipeline-slices.md. Execute o checkpoint 1
(desenhar o script) e pare para revisão. Crie um rascunho de scripts/run-slice.sh em bash
que implemente a lógica descrita, mas não implemente a integração com o agente ainda
(pode ser placeholder). Foque na estrutura: ler spec, iterar checkpoints, pausar para revisão.
```

## Retrospectiva
- O que a IA acertou de primeira: desenho do script como *driver* honesto — não finge executar
  o trabalho do agente, apenas orquestra (lista checkpoints da spec, pausa, verifica, mede).
  Verificações reutilizam os comandos do AGENTS.md incluindo coverage.
- Onde precisou de correção humana: `mapfile` não existe no bash 3.2 padrão do macOS —
  substituído por `while read` loop; `fail()` portado para não abortar a bateria.
- Ajustes feitos no AGENTS.md por causa desta slice: comando `./scripts/run-slice.sh NNN`
  nos comandos de verificação.
- Tempo gasto (estimado) vs. sem IA: ~30min com IA vs. ~1h30 sem IA.
