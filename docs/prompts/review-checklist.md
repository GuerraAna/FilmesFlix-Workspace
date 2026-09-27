# Checklist de revisão de checkpoint

Use como prompt para o agente revisar o próprio trabalho (ou para você revisar o PR):

```
Revise o diff deste checkpoint contra docs/slices/NNN-*.md e AGENTS.md. Responda:
1. O contrato em contracts/openapi.yaml foi respeitado? Liste divergências campo a campo.
2. Algum consumidor no workspace ficou quebrado? (grep pelos paths alterados)
3. Os testes exigidos pelo checkpoint existem e passam?
4. Há código fora do escopo da slice? Aponte e sugira remover.
5. O que deveria entrar no AGENTS.md para evitar o mesmo erro no futuro?
```

## Sinais de alerta comuns
- DTO do app com nome de campo diferente do contrato (ex.: `titulo` vs `title`).
- Lógica de negócio dentro do Controller (BFF) ou da Activity (app).
- Ausência de estado de erro/loading na tela.
- Agente "resolveu" mudando o teste em vez do código.
