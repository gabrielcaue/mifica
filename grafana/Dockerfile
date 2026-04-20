FROM grafana/grafana:11.2.0

USER root

COPY monitoring/grafana/provisioning /etc/grafana/provisioning
COPY monitoring/grafana/dashboards /var/lib/grafana/dashboards
COPY grafana/entrypoint.sh /entrypoint.sh

RUN chown -R 472:472 /etc/grafana/provisioning /var/lib/grafana/dashboards
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]