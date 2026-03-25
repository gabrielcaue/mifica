# Package: `mifica-frontend/src/routes`

## Objetivo
Definir navegação da SPA e política de acesso por rota.

## Escopo
- Inclui: roteamento público/protegido e redirecionamentos.
- Não inclui: chamadas HTTP de domínio.

## Contratos e interfaces
- Rotas devem mapear para páginas válidas e wrappers de autorização.

## Regras e invariantes
- Usuário não autenticado não acessa rotas privadas.
- Rotas administrativas exigem perfil autorizado.

## Critérios de aceitação
- Navegação respeita autenticação e estado de sessão.

## Dependências e integrações
- React Router, `context` de auth, `pages`.

## Riscos e trade-offs
- Condições de acesso duplicadas causam inconsistência; mitigado por wrappers únicos.
