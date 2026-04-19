# Package: `mifica-streamlit/services`

## Objetivo
Centralizar integrações externas consumidas pelo dashboard Streamlit.

## Escopo
- Inclui: chamadas para backend/blockchain e adaptação de resposta.
- Não inclui: renderização de componentes visuais.

## Contratos e interfaces
- Métodos de consumo de API com parâmetros explícitos.

## Regras e invariantes
- Camada de serviço deve isolar HTTP da camada de UI.

## CDD/ICP — campos atualizados
- Requests de transação blockchain enviam somente `destinatario` e `valor`.
- Mensagens para perfil admin devem refletir o limite total de movimentação de `1.000.000`.
- Dados técnicos (`hashTransacao`, `remetente`) não são obrigatórios na UX para operação padrão.

## Critérios de aceitação
- Páginas do painel consomem dados sem repetir código de integração.

## Dependências e integrações
- `streamlit-config`, APIs backend.

## Riscos e trade-offs
- Falha de endpoint impacta painéis; mitigado com tratamento de exceção e fallback de mensagem.
