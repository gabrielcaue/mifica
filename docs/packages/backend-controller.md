# Package: `com.mifica.controller`

## Objetivo
Expor a API REST e traduzir requisições HTTP em casos de uso do domínio.

## Escopo
- Inclui: mapeamento de rotas, validações de entrada, resposta HTTP e delegação para services.
- Não inclui: persistência direta ou regra de negócio complexa.

## Contratos e interfaces
- Contratos JSON (DTOs) para login, cadastro, perfil, transações e gamificação.
- Códigos HTTP coerentes (`200`, `201`, `400`, `401`, `403`, `404`, `500`).

## Regras e invariantes
- Controller não implementa regra de negócio; apenas orquestra e delega.
- Endpoints protegidos devem validar identidade/autorização.

## Critérios de aceitação
- Rotas públicas acessam sem token.
- Rotas protegidas falham sem token válido.
- Respostas expõem apenas campos permitidos.

## Dependências e integrações
- `service`, `dto`, `util` (JWT), Spring Web.

## Riscos e trade-offs
- Acúmulo de lógica em controller reduz coesão; mitigado por separação em services.
