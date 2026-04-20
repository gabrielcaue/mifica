# Package: `mifica-frontend/src/assets`

## Objetivo
Centralizar ativos estáticos utilizados pela interface React (logos e recursos visuais).

## Escopo
- Inclui: imagens e arquivos estáticos referenciados pelos componentes/páginas.
- Não inclui: lógica de renderização ou transformação de mídia.

## Contratos e interfaces
- Consumo por import direto em componentes React.
- Nome de arquivo estável para evitar quebra de import em build.

## Regras e invariantes
- Assets devem ser otimizados para web e manter identidade visual do produto.
- Alteração de nome/caminho requer ajuste dos imports dependentes.

## CDD/ICP — campos atualizados
- Mantém consistência visual das telas onde métricas financeiras e transações foram atualizadas.

## Critérios de aceitação
- Build do frontend resolve imports de assets sem erro.
- Recursos visuais carregam corretamente em produção.

## Dependências e integrações
- Vite (pipeline de build estático) e componentes/páginas React.

## Riscos e trade-offs
- Assets pesados impactam tempo de carregamento; preferir compactação e versionamento controlado.
