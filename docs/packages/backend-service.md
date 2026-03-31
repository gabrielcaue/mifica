# Package: `com.mifica.service`

## Papel na arquitetura
Camada de **casos de uso** e regras de negócio. Orquestra entidades, repositórios e integrações técnicas para entregar comportamento de domínio.

## Responsabilidades
- Implementar regras de negócio centrais.
- Garantir consistência entre operações relacionadas.
- Coordenar integrações (email, JWT, Redis, blockchain) quando necessário.

## Serviços do pacote
| Serviço | Domínio |
|---|---|
| `AuthService` | autenticação e emissão/validação de acesso |
| `UsuarioService` | gestão de conta/perfil |
| `EmailVerificationService` | fluxo de verificação de email |
| `EmailService` | envio de notificações de email |
| `ContratoService` | lifecycle de contratos |
| `TransacaoService` | transações de negócio |
| `DesafioService` | desafios e evolução do usuário |
| `GamificationService` | pontuação, recompensas e eventos gamificados |
| `ReputacaoService` | regras de reputação e histórico |

## Limites (clean architecture)
- **Pode depender de:** `repository`, `entity`, `dto`, `util`, `redis`, integrações externas.
- **Não deve depender de:** detalhes HTTP (request/response específicos).

## Regras e invariantes
- Senhas sempre armazenadas com hash seguro.
- Operações críticas devem ser transacionais quando necessário.
- Reputação/pontos devem manter rastreabilidade em histórico.

## Checklist para mudanças
- Regra nova foi implementada no service (não no controller)?
- Existe validação de consistência e tratamento de erro de domínio?
- Fluxos sensíveis têm teste cobrindo caso feliz e caso de falha?

## Critérios de aceitação
- Casos de uso principais executam de ponta a ponta.
- Dados persistidos permanecem consistentes após operações combinadas.

## Riscos e trade-offs
- Serviços grandes podem virar “god classes”; manter separação por subdomínio é obrigatório.
