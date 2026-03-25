# Package: `com.mifica.config`

## Objetivo
Centralizar configuração transversal da aplicação (segurança, CORS, Redis e web).

## Escopo
- Inclui: beans de segurança, CORS, listeners Redis e wiring de infraestrutura.
- Não inclui: regras de negócio.

## Contratos e interfaces
- Define `SecurityFilterChain` e políticas de autorização.
- Define `CorsConfigurationSource` por variável de ambiente.
- Define container/listener para Redis Pub/Sub.

## Regras e invariantes
- Endpoints públicos e protegidos devem ser explícitos.
- CORS deve aceitar somente origens configuradas.

## Critérios de aceitação
- Preflight `OPTIONS` funciona em produção.
- Rotas protegidas exigem JWT válido.
- App inicia mesmo com indisponibilidade temporária do Redis listener.

## Dependências e integrações
- Spring Security, Redis client e propriedades de ambiente.

## Riscos e trade-offs
- Erros de configuração podem bloquear acesso legítimo (falso negativo de segurança).
