# Package: `com.mifica.repository`

## Objetivo
Abstrair acesso ao banco relacional através de interfaces Spring Data/JPA.

## Escopo
- Inclui: CRUD e consultas derivadas por convenção.
- Não inclui: lógica de negócio.

## Contratos e interfaces
- Métodos de consulta por ID, email e relacionamentos conforme necessidade do domínio.

## Regras e invariantes
- Repositórios não devem aplicar regras de negócio complexas.
- Operações devem respeitar o modelo de entidades.

## Critérios de aceitação
- Consultas retornam dados esperados para os casos de uso.
- Escritas persistem sem quebrar integridade referencial.

## Dependências e integrações
- `entity` e `service`.

## Riscos e trade-offs
- Query methods excessivos podem gerar acoplamento implícito; mitigado com revisão de contratos.
