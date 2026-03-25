# Package: `com.mifica.redis`

## Objetivo
Implementar comunicação assíncrona para eventos de gamificação via Redis Pub/Sub.

## Escopo
- Inclui: publisher de eventos e subscriber consumidor.
- Não inclui: persistência principal de usuários.

## Contratos e interfaces
- Mensagem padrão: evento com `userId` e `points`.
- Canal de publicação centralizado por configuração.

## Regras e invariantes
- Publicação não deve bloquear fluxo HTTP principal.
- Subscriber deve tratar parsing inválido sem derrubar aplicação.

## Critérios de aceitação
- Evento publicado é consumido e processado na lógica de gamificação.
- Falha de Redis não interrompe uso principal da API.

## Dependências e integrações
- `config.RedisConfig`, `service.GamificationService`, `StringRedisTemplate`.

## Riscos e trade-offs
- Pub/Sub não garante histórico nativo; mitigado com persistência de efeitos no banco.
