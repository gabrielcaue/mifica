FROM grafana/grafana:11.2.0

USER root

COPY monitoring/grafana/provisioning/datasources/datasource.railway.yml /tmp/datasource.railway.yml
COPY monitoring/grafana/provisioning /etc/grafana/provisioning
COPY monitoring/grafana/dashboards /var/lib/grafana/dashboards

# Railway: garante apenas 1 datasource provisionada
RUN find /etc/grafana/provisioning/datasources -type f \( -name "*.yml" -o -name "*.yaml" \) -delete \
	&& cp /tmp/datasource.railway.yml /etc/grafana/provisioning/datasources/datasource.yml \
	&& rm -f /tmp/datasource.railway.yml

RUN chown -R 472:472 /etc/grafana/provisioning /var/lib/grafana/dashboards

USER 472