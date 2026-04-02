# Package: `com.mifica.entity`

## Papel na arquitetura
Modelar o estado persistente do negócio (núcleo de dados da aplicação).

## Responsabilidades
- Definir entidades JPA, relacionamentos e enums.
- Representar invariantes estruturais do domínio no nível de dados.
- Servir como base para consultas/transações dos repositórios.

## Entidades do pacote
| Tipo | Elementos |
|---|---|
| Usuário e acesso | `User`, `Usuario`, `Role` |
| Reputação e gamificação | `HistoricoReputacao`, `Badge`, `DesafioGamificado`, `Avaliacao` |
| Negócio financeiro/contratual | `Contrato`, `Transacao`, `SolicitacaoCredito` |

## Limites (clean architecture)
- **Pode depender de:** anotações JPA/Hibernate e tipos utilitários Java.
- **Não deve depender de:** `controller` e objetos HTTP.

## Regras e invariantes
- Chaves e relacionamentos devem manter integridade referencial.
- Valores críticos (datas, valores monetários, status) devem ser validados nas camadas superiores.
- Entidade não deve conter lógica de integração externa.

## Convenções de modelagem
- Evitar side effects em `getters/setters`.
- Evitar acoplamento com serialização externa direta.
- Sempre revisar impacto de `cascade` e `fetch` para desempenho e consistência.

## Critérios de aceitação
- Persistência e recuperação funcionam em cenários de CRUD.
- Relacionamentos se mantêm consistentes em transações.

## Riscos e trade-offs
- Entidades anêmicas simplificam manutenção, mas concentram regra de negócio em `service`.
