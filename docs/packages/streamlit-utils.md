# Package: `mifica-streamlit/utils`

## Objetivo
Concentrar utilidades puras de apoio para autenticação, gráficos e tratamento de dados.

## Escopo
- Inclui: helpers reutilizáveis sem dependência forte de página.
- Não inclui: acesso direto à camada HTTP principal.

## Contratos e interfaces
- Funções com entrada/saída explícitas para composição em páginas.

## Regras e invariantes
- Utilitários devem ser simples, previsíveis e testáveis.

## Critérios de aceitação
- Redução de duplicação de lógica entre painéis.

## Dependências e integrações
- Consumido por `streamlit-components` e páginas.

## Riscos e trade-offs
- Acúmulo de utilitários heterogêneos pode reduzir coesão; revisar por domínio.
