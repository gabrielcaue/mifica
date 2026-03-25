# Package: `mifica-frontend/src/context`

## Objetivo
Centralizar estado global de autenticação e sessão do usuário.

## Escopo
- Inclui: provider de auth, login/logout e leitura de token/usuário.
- Não inclui: requisições HTTP diretas da aplicação.

## Contratos e interfaces
- Exposição de hooks/context para consumir sessão em páginas e componentes.

## Regras e invariantes
- Estado local e storage devem permanecer sincronizados.
- Logout deve limpar credenciais locais.

## Critérios de aceitação
- Rotas protegidas reagem corretamente ao estado autenticado.

## Dependências e integrações
- `services` (api), `routes`.

## Riscos e trade-offs
- Estado global mal invalidado gera sessão inconsistente; mitigado com fluxo único de login/logout.
