# Package: `mifica-frontend/src/pages`

## Objetivo
Implementar as telas de uso final (login, cadastro, dashboard, perfil, admin).

## Escopo
- Inclui: composição de componentes, eventos de formulário e consumo de services.
- Não inclui: configuração global de rotas.

## Contratos e interfaces
- Cada página recebe contexto/estado e aciona serviços com contratos definidos.

## Regras e invariantes
- Página não deve concentrar lógica de infraestrutura além do necessário para UX.

## CDD/ICP — campos atualizados
- Nos formulários de transação em páginas, foram removidos os campos de entrada `hashTransacao` e `remetente`.
- A interface exibe e envia apenas `destinatario` e `valor` para registro de nova transação.
- Nos cards/listagens do dashboard, `hash` e `remetente` não devem ser exibidos ao usuário final.

## Critérios de aceitação
- Fluxos principais de navegação e formulários funcionam ponta a ponta.

## Dependências e integrações
- `components`, `context`, `services`, `routes`.

## Riscos e trade-offs
- Acúmulo de lógica por página pode reduzir legibilidade; mitigado com hooks/components.
