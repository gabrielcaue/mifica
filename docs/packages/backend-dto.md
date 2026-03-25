# Package: `com.mifica.dto`

## Objetivo
Definir contratos de entrada e saída da API sem expor entidades internas.

## Escopo
- Inclui: DTOs de autenticação, usuário, reputação, contrato e blockchain.
- Não inclui: lógica de persistência.

## Contratos e interfaces
- Campos e formatos esperados em request/response.
- Suporte a validação declarativa (quando aplicável).

## Regras e invariantes
- DTO não deve carregar comportamento de domínio.
- Senha e dados sensíveis não devem ser retornados.

## Critérios de aceitação
- API responde com DTOs estáveis e previsíveis.
- Alterações de entidade não quebram contrato externo indevidamente.

## Dependências e integrações
- Utilizado por `controller` e `service`.

## Riscos e trade-offs
- Excesso de DTOs aumenta manutenção, mas reduz acoplamento entre API e JPA.
