# Package: `mifica-frontend/src/services`

## Objetivo
Centralizar cliente HTTP e integração com API backend.

## Escopo
- Inclui: configuração de base URL, interceptors e headers comuns.
- Não inclui: renderização de UI.

## Contratos e interfaces
- Métodos HTTP devem seguir contratos dos endpoints backend.

## Regras e invariantes
- Token JWT deve ser injetado quando existir sessão válida.

## Critérios de aceitação
- Requisições de páginas funcionam sem repetição de configuração.

## Dependências e integrações
- Axios e `context` de autenticação.

## Riscos e trade-offs
- Falha de configuração global afeta toda app; mitigado por inicialização central única.
