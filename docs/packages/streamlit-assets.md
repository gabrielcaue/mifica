# Package: `mifica-streamlit/assets`

## Objetivo
Armazenar os ativos visuais usados no painel Streamlit.

## Escopo
- Inclui: logos e recursos gráficos estáticos.
- Não inclui: lógica de cálculo de métricas ou fluxo de transações.

## Contratos e interfaces
- Consumo por caminho local em scripts Streamlit (`Image.open(...)`).

## Regras e invariantes
- Caminhos dos arquivos devem permanecer compatíveis com os scripts de UI.
- Mudanças visuais não devem impactar regras de negócio.

## CDD/ICP — campos atualizados
- Suporta a apresentação dos novos indicadores financeiros (saldo admin, total movimentado e saldo de usuários comuns).

## Critérios de aceitação
- Painel carrega logo sem falha de arquivo.
- Ambiente containerizado mantém os assets acessíveis.

## Dependências e integrações
- `PIL`/`streamlit` para renderização de imagem.

## Riscos e trade-offs
- Falta de padronização de nomes pode quebrar dashboards após deploy.
