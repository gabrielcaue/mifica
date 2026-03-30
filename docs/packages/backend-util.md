# Package: `com.mifica.util`

## Papel na arquitetura
Conter utilitários técnicos reutilizáveis de baixo acoplamento, principalmente para autenticação baseada em JWT.

## Responsabilidades
- Gerar e validar tokens.
- Extrair claims para autorização/autenticação.
- Fornecer helpers técnicos para camadas de segurança.

## Classes do pacote
| Classe | Responsabilidade |
|---|---|
| `JwtService` | API principal de token para uso em serviços/filtros |
| `JwtUtil` | utilitário de suporte para parsing/claims |
| `JwtFiltro` | componente auxiliar de filtragem por token (quando aplicável no fluxo atual) |

## Limites (clean architecture)
- **Pode depender de:** bibliotecas JWT, configuração de segurança.
- **Não deve depender de:** regras de negócio de domínio.

## Regras e invariantes
- Segredo/chave de assinatura deve vir de configuração externa segura.
- Claims obrigatórias (subject, expiração, papel/perfil quando necessário) precisam ser consistentes.
- Expiração deve ser validada em toda autenticação.

## Convenções de segurança
- Não logar token completo em produção.
- Evitar duplicação de lógica de validação entre classes.
- Padronizar algoritmo de assinatura e tempo de expiração.

## Critérios de aceitação
- Token válido é aceito por filtros de segurança.
- Token inválido/expirado é rejeitado com resposta apropriada.

## Riscos e trade-offs
- Duplicidade de utilitários JWT gera inconsistência e falhas de segurança; manter um fluxo canônico.
