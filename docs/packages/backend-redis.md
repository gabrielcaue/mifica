# Package: `com.mifica.redis`

## Papel na arquitetura
Camada de mensageria assíncrona para eventos de gamificação usando Redis Pub/Sub.

## Responsabilidades
- Publicar eventos de pontos/reputação em canal Redis.
- Consumir eventos e acionar processamento de domínio.
- Desacoplar fluxos síncronos da API de processamento secundário.

## Classes do pacote
| Classe | Responsabilidade |
|---|---|
| `GamificationPublisher` | Publica mensagens de gamificação |
| `GamificationSubscriber` | Consome mensagens e delega ao serviço |

## Limites (clean architecture)
- **Pode depender de:** `config.RedisConfig`, `service.GamificationService`, cliente Redis.
- **Não deve depender de:** controllers.

## Contrato de mensagem
- Payload deve conter contexto mínimo para processamento (ex.: `userId`, `points`, tipo de evento).
- Formato de mensagem deve permanecer versionável e documentado.

## Regras e invariantes
- Publicação não pode bloquear operação principal da API.
- Erros de consumo devem ser tratados sem derrubar aplicação.
- Processamento precisa ser idempotente quando aplicável.

## Critérios de aceitação
- Evento publicado chega ao subscriber.
- Falha temporária de Redis não interrompe funcionalidades centrais do backend.

## Riscos e trade-offs
- Pub/Sub puro não oferece retenção/histórico nativo; exigir persistência do efeito no domínio.
