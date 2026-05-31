#!/bin/bash
# Script para rodar testes com OpenJDK 17 (compatível com CI local)

export JAVA_HOME="/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

# Opções JVM para contornar problema de certificados do Homebrew
export MAVEN_OPTS="-Djava.security.manager=allow -Dcom.sun.jndi.ldap.connect.pool=false"

cd "$(dirname "$0")"

# Rodar testes
./mvnw clean test -e 2>&1 | tee test-output.log

echo ""
echo "=========================================="
echo "Testes completados!"
echo "Log salvo em: test-output.log"
echo "=========================================="
