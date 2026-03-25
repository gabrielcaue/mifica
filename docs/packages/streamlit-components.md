# Package: `mifica-streamlit/components`

## Objetivo
Reunir blocos visuais reutilizáveis para o painel Streamlit.

## Escopo
- Inclui: componentes de interface reutilizáveis.
- Não inclui: integração HTTP externa.

## Contratos e interfaces
- Componentes recebem parâmetros explícitos e retornam renderização previsível.

## Regras e invariantes
- Reuso visual sem duplicar estrutura de tela.

## Critérios de aceitação
- Painéis compartilham componentes sem divergência de layout.

## Dependências e integrações
- `streamlit-utils`, `streamlit-config`.

## Riscos e trade-offs
- Componentes muito específicos reduzem reutilização.
