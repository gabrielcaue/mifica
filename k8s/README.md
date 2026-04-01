# Kubernetes do Mifica

Este diretório adiciona uma base Kubernetes para o projeto com:

- `Deployment` + `Service` para backend, frontend e streamlit
- `Deployment` + `Service` + `PVC` para mysql e redis
- `Ingress` para roteamento (`/api`, `/streamlit`, `/`)
- exemplos de `Pod` isolado em `k8s/pods/`

## Estrutura

- `namespace.yaml` → namespace `mifica`
- `configmap.yaml` → variáveis não sensíveis
- `secret.example.yaml` → modelo de segredos (não aplicar sem ajustar)
- `secret.yaml` → segredo real baseado no `.env`
- `mysql.yaml`, `redis.yaml` → dados de suporte
- `backend.yaml`, `frontend.yaml`, `streamlit.yaml` → aplicações
- `ingress.yaml` → entrada HTTP
- `kustomization.yaml` → aplicação em lote

## Como aplicar

1. Criar segredo real a partir do exemplo:

   - copie `secret.example.yaml` para `secret.yaml`
   - preencha valores reais

2. Aplicar recursos:

   - aplicar namespace/config/secret/deploy/services/ingress via `kustomization.yaml`

## Pods isolados

Em `k8s/pods/` há manifests diretos de `Pod` para teste rápido.

> Para produção, prefira `Deployment` (self-healing, rollout e escalabilidade).
