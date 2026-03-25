# Package: `com.mifica.entity`

## Objetivo
Representar o modelo persistente do domínio (usuários, transações, contratos e afins).

## Escopo
- Inclui: entidades JPA, relacionamentos e enumerações do domínio.
- Não inclui: regras de transporte HTTP.

## Contratos e interfaces
- Mapeamento ORM para tabelas e colunas.
- Relações `OneToMany`, `ManyToOne`, `ElementCollection` conforme domínio.

## Regras e invariantes
- Integridade referencial entre agregados.
- Campos obrigatórios devem ser respeitados pela camada de serviço.

## Critérios de aceitação
- Entidades persistem e recuperam corretamente via repositórios.
- Relacionamentos mantêm consistência nas operações de escrita.

## Dependências e integrações
- JPA/Hibernate e camada repository.

## Riscos e trade-offs
- Entidades muito ricas podem acoplar domínio à persistência; mitigado com DTO/service.
