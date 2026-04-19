# Package: `mifica-streamlit` (arquivos raiz)

## Objetivo
Documentar os scripts raiz do painel Streamlit (`app.py`, `dashboard.py`, `blockchain_panel.py`) que orquestram UI e fluxo de transações.

## Escopo
- Inclui: composição de telas, mensagens de regra de negócio e orquestração de chamadas para API.
- Não inclui: implementação de clientes HTTP utilitários (camada `services`).

## Contratos e interfaces
- Entrada de transação em UI: `destinatario` e `valor`.
- Uso de token de sessão (`st.session_state['token']`) para autorização nas rotas backend.

## Regras e invariantes
- Usuário comum: transferência apenas para usuário comum (mensagem orientativa).
- Admin: transferência para qualquer perfil, respeitando limite total de `1.000.000` no backend.
- UI não deve exigir `hashTransacao` nem `remetente` para criar transação.

## CDD/ICP — campos atualizados
- Campos removidos da UI: `hashTransacao`, `remetente`.
- Campos mantidos para operação: `destinatario`, `valor`.
- Listagens de transações focam apenas em dados de negócio visíveis ao usuário.

## Critérios de aceitação
- Formulário registra transação com payload mínimo e autenticação válida.
- Mensagens exibidas em Streamlit estão aderentes às regras atuais de admin/comum.

## Dependências e integrações
- `requests` para integração HTTP.
- Endpoints backend `/api/blockchain/transacoes`.

## Riscos e trade-offs
- Duplicação parcial de fluxo entre scripts raiz pode gerar drift de comportamento se não houver manutenção coordenada.
