#!/bin/bash

# Script para configurar Java 17 no VS Code

JAVA17_PATH="/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"

echo "🔧 Configurando Java 17 para VS Code..."

# Verificar se Java 17 existe
if [ ! -d "$JAVA17_PATH" ]; then
    echo "❌ Java 17 não encontrado em: $JAVA17_PATH"
    echo "   Instale com: brew install openjdk@17"
    exit 1
fi

echo "✅ Java 17 encontrado: $JAVA17_PATH"

# Criar diretório .vscode se não existir
mkdir -p /Users/user/mifica/mifica-backend/.vscode

# Criar settings.json para Java 17
cat > /Users/user/mifica/mifica-backend/.vscode/settings.json << 'EOF'
{
  "redhat.telemetry.enabled": true,
  "java.import.gradle.enabled": false,
  "java.home": "/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home",
  "java.jdt.ls.java.home": "/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home",
  "[java]": {
    "editor.defaultFormatter": "redhat.java",
    "editor.formatOnSave": true
  },
  "java.debug.settings.hotCodeReplace": "auto",
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-17",
      "path": "/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
    }
  ],
  "java.project.sourcePaths": ["src/main/java", "src/test/java"],
  "java.test.config": {
    "name": "maven",
    "args": ["clean", "test"]
  }
}
EOF

echo "✅ Configuração criada em: .vscode/settings.json"

echo ""
echo "📝 Para aplicar as mudanças:"
echo "   1. Feche VS Code"
echo "   2. Execute: code /Users/user/mifica/mifica-backend"
echo "   3. Aguarde 30 segundos para re-indexing"
echo "   4. Os erros em vermelho devem desaparecer"

echo ""
echo "🧪 Para validar que os testes funcionam:"
echo "   $ cd /Users/user/mifica/mifica-backend"
echo "   $ source .env"
echo "   $ mvn clean test"

echo ""
echo "✨ Pronto!"
