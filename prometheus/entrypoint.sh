#!/bin/sh
set -e

: "${BACKEND_TARGET:=mifica-backend:8080}"
: "${BACKEND_SCHEME:=http}"

sed "s|__BACKEND_TARGET__|${BACKEND_TARGET}|g" /etc/prometheus/prometheus.railway.yml > /etc/prometheus/prometheus.yml
sed -i "s|__BACKEND_SCHEME__|${BACKEND_SCHEME}|g" /etc/prometheus/prometheus.yml

exec prometheus \
  --config.file=/etc/prometheus/prometheus.yml \
  --storage.tsdb.path=/prometheus