# Package: `mifica-flutter/lib/config`

## Objetivo
Centralizar configuração técnica do app Flutter e cliente HTTP.

## Escopo
- Inclui: base URL, headers, token storage e comportamento padrão de requisição.
- Não inclui: regras de UI/páginas.

## Contratos e interfaces
- Cliente de API com métodos padronizados (`get`, `post`, etc.).

## Regras e invariantes
- JWT deve ser lido/escrito em armazenamento seguro.
- Requisições autenticadas devem enviar header `Authorization` quando token existir.

## Critérios de aceitação
- App mobile autentica e consome API sem duplicar configuração por tela.

## Dependências e integrações
- Backend REST e Flutter Secure Storage.

## Riscos e trade-offs
- Base URL fixa por ambiente exige disciplina em release profile.
