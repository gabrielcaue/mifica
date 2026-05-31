**Resumo visual (Camunda BPM)**

Arquivo criado: `docs/guardrails.bpmn`

Como usar:

- Abra o Camunda Modeler (ou outro editor BPMN 2.0).
- No Modeler: `File > Open File...` e selecione `docs/guardrails.bpmn`.
- O diagrama mostra o fluxo alto-nível dos guardrails implementados:
  1. `Validate Input (Jakarta Validation)` — Validação dos DTOs na entrada.
  2. `Global Exception Handler` — Padronização de erros e logs.
  3. `Rate Limiting (Redis)` — Proteção contra abuso por IP.
  4. `Resilience: Timeouts & Circuit Breakers` — Timeouts, retries e CB.
  5. Gateway para observability que bifurca em `Health Checks` e `Audit Logging`.
  6. `CSP (Frontend headers)` — Diretrizes aplicadas ao frontend.

Notas:
- O BPMN é apenas um diagrama de alto nível para arquitetura e onboarding.
- Use-o para apresentações, documentação e para discutir rotações/ações operacionais.
- Se quiser, posso gerar uma versão PNG exportada do diagrama (requer Camunda Modeler local ou um conversor BPMN).