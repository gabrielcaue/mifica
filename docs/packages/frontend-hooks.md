# Package: `mifica-frontend/src/hooks`

## Objetivo
Isolar comportamentos reutilizáveis de UI e estado em hooks customizados.

## Escopo
- Inclui: hooks de responsividade e utilidades de comportamento.
- Não inclui: renderização de componentes completos.

## Contratos e interfaces
- Hooks retornam estado derivado e ações mínimas necessárias.

## Regras e invariantes
- Hook deve ser idempotente e sem efeitos colaterais inesperados.

## Critérios de aceitação
- Páginas reutilizam lógica sem duplicação e sem regressão funcional.

## Dependências e integrações
- React state/effects e `components`.

## Riscos e trade-offs
- Hooks muito genéricos podem ficar difíceis de testar e evoluir.
