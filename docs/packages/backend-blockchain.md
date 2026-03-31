# Package: `com.mifica.blockchain`

## Papel na arquitetura
Camada de integração e rastreabilidade para eventos/transações ligadas ao contexto blockchain da plataforma.

## Responsabilidades
- Registrar transações blockchain no banco para auditoria.
- Expor operações de leitura para histórico e monitoramento.
- Isolar detalhes desse domínio do restante da aplicação.

## Classes do pacote
| Classe | Tipo | Responsabilidade |
|---|---|---|
| `BlockchainService` | serviço | Caso de uso para gravação/consulta de transações blockchain |
| `TransacaoBlockchain` | entidade | Modelo persistente de transação blockchain |
| `TransacaoBlockchainRepository` | repositório | Acesso a dados da entidade blockchain |

## Limites (clean architecture)
- **Pode depender de:** `dto`, JPA, infraestrutura transacional.
- **Não deve depender de:** controllers externos ao domínio blockchain e regras de autenticação.

## Regras e invariantes
- `hash` da transação deve ser tratado como identificador técnico de auditoria.
- Campos obrigatórios (`remetente`, `destinatario`, `valor`, `data`) não podem ser nulos.
- Datas devem ser registradas no backend para padronização temporal.

## Contratos de integração
- Entrada típica: `TransacaoBlockchainDTO`.
- Saída típica: confirmação persistida com `id` interno + metadados.

## Checklist para mudanças
- Alteração preserva rastreabilidade (hash + timestamps)?
- Queries continuam performáticas para histórico?
- Mudança impacta consistência com eventos on-chain?

## Critérios de aceitação
- Criação de transação persiste sem perda de metadados.
- Consulta retorna histórico consistente e auditável.

## Riscos e trade-offs
- Divergência entre fonte on-chain e base relacional local exige estratégia de reconciliação.
