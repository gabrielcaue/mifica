#!/bin/sh
set -e

: "${PROMETHEUS_URL:=http://prometheus:9090}"

cp /etc/grafana/provisioning/datasources/datasource.railway.yml /etc/grafana/provisioning/datasources/datasource.yml
sed -i "s|__PROMETHEUS_URL__|${PROMETHEUS_URL}|g" /etc/grafana/provisioning/datasources/datasource.yml

exec /run.sh