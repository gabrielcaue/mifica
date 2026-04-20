FROM prom/prometheus:v2.54.1

USER root

COPY monitoring/prometheus/prometheus.railway.yml /etc/prometheus/prometheus.railway.yml
COPY prometheus/entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
