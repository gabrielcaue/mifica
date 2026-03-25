# Package: `mifica-streamlit/config`

## Objetivo
Centralizar parâmetros de ambiente e configuração do painel Streamlit.

## Escopo
- Inclui: variáveis de configuração e defaults.
- Não inclui: lógica de apresentação.

## Contratos e interfaces
- API interna de leitura de configurações para módulos do painel.

## Regras e invariantes
- Configuração deve ser única e reutilizável entre telas.

## Critérios de aceitação
- Mudança de ambiente não exige alteração de regra de negócio nas páginas.

## Dependências e integrações
- Utilizado por `services`, `utils` e páginas Streamlit.

## Riscos e trade-offs
- Defaults incorretos podem mascarar erro de ambiente.
