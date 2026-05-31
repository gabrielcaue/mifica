# BPMN Exporter

Gera `PNG` e `SVG` a partir de um arquivo `.bpmn`.

## Uso rápido

```bash
cd /Users/user/mifica/tools/bpmn-exporter
npm install
npm run export
```

Isso gera:

- `docs/guardrails.png`
- `docs/guardrails.svg`

## Uso manual

```bash
node render-bpmn.mjs ../../docs/guardrails.bpmn ../../docs/guardrails
```
