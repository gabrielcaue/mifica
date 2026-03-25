# Package: `com.mifica.util`

## Objetivo
Concentrar utilitários técnicos reutilizáveis, principalmente relacionados a JWT.

## Escopo
- Inclui: geração, validação e extração de claims de tokens.
- Não inclui: regras de negócio de usuário.

## Contratos e interfaces
- APIs utilitárias para controllers/filtros consumirem autenticação.

## Regras e invariantes
- Token deve ser assinado com chave configurada por ambiente.
- Claims mínimos (subject/role) devem ser consistentes.

## Critérios de aceitação
- Token gerado é validável pela aplicação.
- Extração de email/role funciona para payloads válidos.

## Dependências e integrações
- bibliotecas JWT e `repository` (quando necessário para claims).

## Riscos e trade-offs
- Duplicidade de utilitários JWT pode gerar divergência; mitigado por padronização.
