# Package: `mifica-frontend/src/utils`

## Objetivo
Disponibilizar funções auxiliares puras para uso transversal no frontend.

## Escopo
- Inclui: cálculos, formatações e helpers sem efeito colateral.
- Não inclui: chamadas HTTP ou estado global.

## Contratos e interfaces
- Funções devem ter entrada/saída explícitas e previsíveis.

## Regras e invariantes
- Utilitários devem ser deterministicamente testáveis.

## Critérios de aceitação
- Lógica repetida deixa de estar duplicada em páginas/componentes.

## Dependências e integrações
- Consumido por `pages` e `components`.

## Riscos e trade-offs
- Funções utilitárias inchadas perdem foco; manter granularidade por responsabilidade.
