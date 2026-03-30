# Package: `com.mifica.config`

## Papel na arquitetura
Pacote de **infraestrutura transversal**: segurança, CORS, Redis, Web3 e configurações HTTP globais.

## Responsabilidades
- Declarar beans de configuração do Spring.
- Definir política de autenticação/autorização.
- Configurar integrações técnicas (Redis/Web3/WebMvc/CORS).

## Classes do pacote
| Classe | Responsabilidade |
|---|---|
| `SecurityConfig` | Define cadeia de filtros e regras de segurança |
| `CorsConfig` | Define política de origem, headers e métodos permitidos |
| `RedisConfig` | Configura pub/sub e componentes Redis |
| `Web3Config` | Configura integração com stack blockchain/Web3 |
| `WebConfig` | Ajustes globais de configuração web |

## Limites (clean architecture)
- **Pode depender de:** bibliotecas de infraestrutura (Spring Security, Redis, Web3j).
- **Não deve depender de:** regras de negócio de `service`.

## Regras e invariantes
- Rotas públicas vs protegidas devem ficar explícitas e revisáveis.
- CORS não deve usar curingas permissivos em produção.
- Configuração sensível deve vir de variável de ambiente/propriedades.

## Checklist para mudanças
- Alterou regras de segurança? atualizar matriz de endpoints públicos/protegidos.
- Alterou CORS? validar fluxo browser real (preflight + credential).
- Alterou Redis/Web3? validar startup degradado (sem derrubar app inteiro).

## Critérios de aceitação
- `OPTIONS` preflight responde corretamente.
- Endpoints protegidos retornam `401/403` quando esperado.
- Aplicação sobe com perfil local e produção.

## Riscos e trade-offs
- Configuração excessivamente restritiva gera bloqueio de usuários válidos.
- Configuração excessivamente permissiva aumenta superfície de ataque.
