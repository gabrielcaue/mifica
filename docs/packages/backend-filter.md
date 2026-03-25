# Package: `com.mifica.filter`

## Objetivo
Aplicar autenticação no pipeline HTTP antes dos controllers.

## Escopo
- Inclui: filtro de autenticação JWT na cadeia do Spring Security.
- Não inclui: autorização por regra de negócio.

## Contratos e interfaces
- Lê `Authorization: Bearer <token>`.
- Define contexto de autenticação em `SecurityContext` quando token válido.

## Regras e invariantes
- Não autenticar quando token inválido/ausente em rota protegida.
- Não interromper rotas públicas explicitamente liberadas.

## Critérios de aceitação
- Requests autenticadas propagam usuário/autoridade corretamente.
- Requests inválidas retornam status adequado pelo security chain.

## Dependências e integrações
- `util.JwtService` e Spring Security.

## Riscos e trade-offs
- Falha no parse do token pode gerar falso 401/403; mitigado por tratamento defensivo.
