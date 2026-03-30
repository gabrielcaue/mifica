# Package: `com.mifica.filter`

## Papel na arquitetura
Executar autenticação técnica no início do pipeline HTTP, antes da execução dos controllers.

## Responsabilidades
- Extrair e validar token JWT de requests.
- Popular `SecurityContext` para autorização posterior.
- Garantir comportamento idempotente por requisição.

## Classes do pacote
| Classe | Responsabilidade |
|---|---|
| `JwtAuthenticationFilter` | Filtro principal de autenticação por JWT |

## Limites (clean architecture)
- **Pode depender de:** `util` (JWT), Spring Security.
- **Não deve depender de:** casos de uso de negócio (`service`).

## Regras e invariantes
- Header esperado: `Authorization: Bearer <token>`.
- Token inválido não autentica usuário.
- Rotas públicas configuradas no `SecurityConfig` não devem ser quebradas por este pacote.

## Checklist para mudanças
- Falha de token gera resposta correta (`401`/`403`)?
- Contexto de autenticação é limpo em caso inválido?
- Filtro permanece performático (sem chamadas caras por request)?

## Critérios de aceitação
- Request válida propaga principal e authorities.
- Request sem token em rota protegida é rejeitada corretamente.

## Riscos e trade-offs
- Erros de parsing podem causar falso negativo de autenticação; exigir tratamento defensivo e logs claros.
