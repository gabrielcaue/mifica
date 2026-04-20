#!/bin/sh
set -e

: "${BACKEND_TARGET:=mifica-backend:8080}"

sed "s|__BACKEND_TARGET__|${BACKEND_TARGET}|g" /etc/prometheus/prometheus.railway.yml > /etc/prometheus/prometheus.yml

exec prometheus \
  --config.file=/etc/prometheus/prometheus.yml \
  --storage.tsdb.path=/prometheus