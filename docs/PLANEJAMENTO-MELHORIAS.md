# Planejamento para atingir 10/10 no projeto

## Objetivo
Cobrir 100% dos pontos de melhoria identificados na avaliação do projeto FilmesFlix-Workspace.

## Pontos de melhoria identificados

1. **Execução incompleta da slice 000** (6/10)
   - Critérios de aceite não marcados
   - Retrospectiva vazia
   - CI não evidenciada

2. **Testes de contrato ausentes na prática** (6/10)
   - Planejado no AGENTS.md, mas não implementado
   - Crítico para validar contract-first

3. **Complexidade vs. objetivo de aprendizado** (7/10)
   - Não é um problema, mas pode ser mitigado com automação

4. **Falta métricas de sucesso** (7/10)
   - Retrospectivas não preenchidas
   - Sem medição de eficiência dos agentes

5. **Coverage de testes não visível** (6/10)
   - Sem JaCoCo ou similar
   - Métrica importante para qualidade

## Plano de ação (4 novas slices)

### Slice 004: Completar setup inicial
**Objetivo:** Finalizar a slice 000 de forma auditável e preencher retrospectivas das slices anteriores.

**Checkpoints:**
1. Verificar CI existente (`.github/workflows/`)
2. Marcar critérios de aceite da 000
3. Preencher retrospectiva da 000
4. Preencher retrospectivas da 001 e 002 (baseado em histórico)
5. Atualizar roadmap do README

**Entregáveis:**
- CI funcionando (ou documentação de por que não)
- Slice 000 com checkboxes marcados e retrospectiva preenchida
- Slices 001 e 002 com retrospectivas preenchidas
- README atualizado

**Impacto na nota:** +1.5 (de 6/10 para 7.5/10 em "execução")

---

### Slice 005: Testes de contrato
**Objetivo:** Implementar validação automatizada de DTOs do app e BFF contra OpenAPI.

**Checkpoints:**
1. Pesquisar ferramenta de validação (openapi-generator, reflection, etc.)
2. Implementar validação no BFF
3. Implementar validação no app
4. Integrar no pipeline (AGENTS.md + CI)
5. Documentar

**Entregáveis:**
- Teste no BFF validando DTOs vs OpenAPI
- Teste no app validando DTOs vs OpenAPI
- Testes falham se contrato/drift inconsistente
- AGENTS.md atualizado com comando de verificação

**Impacto na nota:** +2.0 (de 6/10 para 8/10 em "testes de contrato")

---

### Slice 006: Coverage reports
**Objetivo:** Adicionar JaCoCo para gerar reports de cobertura nos dois módulos.

**Checkpoints:**
1. Configurar JaCoCo no BFF (thresholds: 70% overall, 80% domain)
2. Configurar JaCoCo no app (verificar compatibilidade Android)
3. Integrar no pipeline (AGENTS.md + CI)
4. Excluir código não crítico (DTOs, Views, etc.)
5. Documentar

**Entregáveis:**
- `./gradlew jacocoTestReport` gera reports em ambos os módulos
- Thresholds configurados
- Comando de verificação no AGENTS.md
- CI inclui step de coverage

**Impacto na nota:** +2.0 (de 6/10 para 8/10 em "coverage")

---

### Slice 007: Automatizar pipeline de slices
**Objetivo:** Criar script que automatiza execução de checkpoints com medição de tempo.

**Checkpoints:**
1. Desenhar o script (`scripts/run-slice.sh`)
2. Implementar execução de checkpoints
3. Integrar com retrospectives (preencher automaticamente)
4. Documentar (AGENTS.md)
5. Testar em uma slice real

**Entregáveis:**
- Script que executa checkpoints sequencialmente
- Pausas para revisão humana
- Registro de tempo por checkpoint
- Preenchimento automático de retrospectiva
- Documentação de uso

**Impacto na nota:** +1.5 (de 7/10 para 8.5/10 em "métricas de sucesso")

---

## Ordem recomendada de execução

1. **Slice 004 primeiro** — fecha o ciclo das slices anteriores, preenche retrospectivas
2. **Slice 005 em seguida** — testes de contrato são críticos para contract-first
3. **Slice 006 depois** — coverage é qualidade, mas não bloqueia desenvolvimento
4. **Slice 007 por último** — automação depende das slices anteriores estarem completas

## Impacto total na nota

| Ponto de melhoria | Nota atual | Nota após | Delta |
|---|---|---|---|
| Execução slice 000 | 6/10 | 7.5/10 | +1.5 |
| Testes de contrato | 6/10 | 8/10 | +2.0 |
| Métricas de sucesso | 7/10 | 8.5/10 | +1.5 |
| Coverage de testes | 6/10 | 8/10 | +2.0 |

**Nota final estimada: 9.5/10** (de 8.5/10 atual)

Os 0.5 pontos restantes podem ser alcançados com:
- Documentação adicional (ex: ADRs para decisões arquiteturais)
- Melhor visualização de reports (ex: badge de coverage no README)
- Monitoramento contínuo (ex: integração com SonarQube ou similar)

## Próximos passos

1. Revisar as specs das slices 004-007 criadas
2. Ajustar se necessário
3. Começar execução pela slice 004
