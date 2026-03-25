# Package: `mifica-frontend/src/components`

## Objetivo
Disponibilizar componentes de UI reutilizáveis para composição das páginas.

## Escopo
- Inclui: componentes visuais e wrappers de navegação/proteção.
- Não inclui: orquestração de rotas globais.

## Contratos e interfaces
- Props explícitas e previsíveis para cada componente.

## Regras e invariantes
- Componentes devem ser reutilizáveis e sem acoplamento desnecessário a páginas específicas.

## Critérios de aceitação
- Páginas reutilizam componentes sem duplicação de markup/lógica visual.

## Dependências e integrações
- `hooks`, `context`, `styles`.

## Riscos e trade-offs
- Componentes genéricos demais podem perder clareza; equilíbrio entre reuso e legibilidade.
