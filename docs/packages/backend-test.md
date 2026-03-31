# Package: `com.mifica` (testes)

## Papel na arquitetura
Garantir qualidade e regressão controlada do backend por meio de testes automatizados de integração e inicialização.

## Responsabilidades
- Validar comportamento de fluxos críticos com dependências reais ou semi-reais.
- Verificar se integrações (ex.: Redis/gamificação) permanecem funcionais.
- Detectar quebras de configuração no startup da aplicação.

## Classes de teste atuais
| Classe | Objetivo |
|---|---|
| `GamificationRedisIntegrationTest` | Validar integração de gamificação com Redis/pub-sub |
| `MificaBackendApplicationTests` | Garantir que o contexto Spring carrega sem falhas |

## Limites (clean architecture)
- **Pode depender de:** qualquer camada necessária para validar cenário real.
- **Não deve depender de:** dados sensíveis fixos em código, endpoints externos não controlados sem isolamento.

## Regras e invariantes
- Testes devem ser determinísticos e reproduzíveis.
- Cenários de integração precisam limpar/isoliar estado sempre que possível.
- Falhas devem indicar causa de forma objetiva (mensagens e asserts claros).

## Boas práticas para evolução
- Para cada regra crítica nova em `service`, adicionar teste correspondente.
- Evitar testes frágeis baseados em timing sem tolerância.
- Priorizar cobertura de fluxos de autenticação, reputação e contratos.

## Critérios de aceitação
- `mvn test` executa sem regressões.
- Testes de integração críticos representam cenários reais do backend.

## Riscos e trade-offs
- Testes de integração podem ser mais lentos, mas reduzem risco de falhas em produção.