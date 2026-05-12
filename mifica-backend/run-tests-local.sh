#!/bin/bash
# Script robusto para rodar testes ignorando problemas de java.security

# Diretórios
BASEDIR=$(cd "$(dirname "$0")" && pwd)
cd "$BASEDIR"

# Opções Maven para contornar problemas de certificados/segurança
export MAVEN_OPTS="-Djava.security.manager=allow -Dcom.sun.jndi.ldap.connect.pool=false -Djava.security.policy=/dev/null"

# Rodar testes com compilação
echo "======================================"
echo "Executando testes unitários e de integração"
echo "======================================"

mvn clean test \
  -DskipTests=false \
  -Dspring.jpa.database-platform=org.hibernate.dialect.H2Dialect \
  -Dspring.h2.console.enabled=true \
  -Dlogging.level.com.mifica=DEBUG \
  -e

echo ""
echo "======================================"
echo "Testes completados!"
echo "======================================"
