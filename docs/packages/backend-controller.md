# Package: `com.mifica.controller`

## Papel na arquitetura
Camada de entrada HTTP da aplicação. Converte requisições REST em chamadas para casos de uso (`service`) e devolve respostas padronizadas.

## Responsabilidades
- Definir endpoints, verbos HTTP e códigos de resposta.
- Validar payload e parâmetros de forma leve.
- Delegar integralmente regra de negócio para `service`.

## Classes do pacote
| Classe | Domínio principal |
|---|---|
| `AuthController` | autenticação/login/cadastro |
| `UsuarioController` | gestão de usuário/perfil |
| `ContratoController` | operações de contratos |
| `DesafioController` | desafios e progresso |
| `TransacaoController` | transações de domínio |
| `GamificationController` | pontos/recompensas/gamificação |
| `BlockchainController` | endpoints de blockchain |
| `SecureController` | rotas protegidas de validação de segurança |
| `HomeController` | endpoint(s) institucionais/health simples |
| `ConfigController` | leitura/exposição de configurações controladas |

## Limites (clean architecture)
- **Pode depender de:** `dto`, `service`, componentes web/segurança.
- **Não deve depender de:** `repository` diretamente.

## Regras e invariantes
- Controller não persiste dados diretamente.
- Requisições autenticadas devem depender de contexto de segurança válido.
- Respostas nunca devem expor credenciais ou dados sensíveis.

## Padrões obrigatórios
- Usar DTO de entrada e saída.
- Tratar erros de forma consistente (`4xx` para erro do cliente, `5xx` para erro interno).
- Manter métodos curtos e orientados a endpoint.

## Critérios de aceitação
- Rotas públicas funcionam sem token.
- Rotas protegidas bloqueiam acesso inválido.
- Contratos JSON permanecem estáveis entre versões compatíveis.

## Riscos e trade-offs
- Lógica em excesso em controller reduz testabilidade e manutenibilidade.
