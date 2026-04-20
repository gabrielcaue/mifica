# Package: `mifica-streamlit/data`

## Objetivo
Concentrar dados locais auxiliares consumidos por módulos do painel Streamlit.

## Escopo
- Inclui: arquivos de dados estáticos (ex.: `usuarios.json`) para apoio de telas e testes locais.
- Não inclui: dados transacionais oficiais do backend em produção.

## Contratos e interfaces
- Leitura por utilitários Python da camada Streamlit.
- Estrutura de JSON deve manter campos esperados pelos componentes consumidores.

## Regras e invariantes
- Dados locais são complementares e não substituem a fonte oficial da API backend.
- Alterações de schema exigem atualização dos leitores e validações.

## CDD/ICP — campos atualizados
- Métricas de saldo/movimentação devem priorizar dados de `blockchain/transacoes` da API.
- Dados locais devem ser usados apenas como suporte não transacional.

## Critérios de aceitação
- Leitura de arquivo não gera exceção no startup do painel.
- Estrutura mínima dos dados atende ao consumo existente.

## Dependências e integrações
- Módulos utilitários do Streamlit (`utils/*`) e bibliotecas padrão de JSON.

## Riscos e trade-offs
- Divergência entre dados locais e backend pode confundir análise se não houver distinção clara.
