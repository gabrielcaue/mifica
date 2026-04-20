# Package: `tools/spec-kit`

## Objetivo
Documentar a toolbox SpecKit usada como referência para especificação orientada por artefatos e automações de engenharia.

## Escopo
- Inclui: templates, presets, scripts e documentação interna do SpecKit.
- Não inclui: regras de negócio do domínio Mifica.

## Contratos e interfaces
- Integração por arquivos markdown, presets e utilitários de suporte ao fluxo de especificação.
- Uso orientado a convenções de estrutura e nomenclatura do próprio kit.

## Regras e invariantes
- Artefatos do kit devem permanecer versionados e rastreáveis.
- Personalizações locais devem ser documentadas para evitar drift em atualizações do toolkit.

## CDD/ICP — campos atualizados
- SDD dos pacotes do projeto segue estrutura compatível com abordagem spec-driven do toolkit.
- Atualizações recentes de regras financeiras (limite admin e saldos) foram refletidas em documentação de pacotes.

## Critérios de aceitação
- Time consegue localizar padrões SDD/ICP a partir do toolkit e aplicar no repositório.
- Estrutura de documentação permanece consistente entre backend, frontend e Streamlit.

## Dependências e integrações
- Diretório `docs/packages` e práticas de engenharia documental do projeto.

## Riscos e trade-offs
- Excesso de documentação sem manutenção contínua pode desalinhar implementação e especificação.
