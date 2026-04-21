FROM grafana/grafana:11.2.0

USER root

COPY monitoring/grafana/provisioning /etc/grafana/provisioning
COPY monitoring/grafana/dashboards /var/lib/grafana/dashboards

# Railway: evita conflito de datasource default (local + railway)
RUN rm -f /etc/grafana/provisioning/datasources/datasource.yml \
	&& cp /etc/grafana/provisioning/datasources/datasource.railway.yml /etc/grafana/provisioning/datasources/datasource.yml \
	&& rm -f /etc/grafana/provisioning/datasources/datasource.railway.yml

RUN chown -R 472:472 /etc/grafana/provisioning /var/lib/grafana/dashboards

USER 472