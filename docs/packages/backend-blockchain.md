# Package: `com.mifica.blockchain`

## Objetivo
Encapsular operações de registro/consulta de transações blockchain no domínio do projeto.

## Escopo
- Inclui: `BlockchainService`, entidade e repositório de transação blockchain.
- Não inclui: autenticação HTTP ou regras gerais de usuário.

## Contratos e interfaces
- Entrada/saída por DTOs de transação blockchain.
- Persistência de hash, remetente, destinatário, valor e data.

## Regras e invariantes
- Timestamp de transação é definido no backend.
- Campos essenciais não podem ser nulos no fluxo de persistência.

## Critérios de aceitação
- Registrar transação retorna DTO persistido com ID.
- Listagem retorna histórico ordenável para auditoria.

## Dependências e integrações
- JPA repository.

## Riscos e trade-offs
- Dependência de consistência entre metadados on-chain e base relacional local.
