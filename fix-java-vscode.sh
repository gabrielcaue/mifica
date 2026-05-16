#!/bin/bash

# Script para corrigir configuração Java do VS Code
# Sem alterar o Java padrão do sistema

echo "🔍 Analisando Java no sistema..."

# Procurar Java 17 instalado
JAVA17_PATHS=(
    "/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home"
    "/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home"
    "/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
    "$HOME/.jdks/openjdk-17"
)

FOUND_JAVA17=""

for path in "${JAVA17_PATHS[@]}"; do
    if [ -d "$path" ] && [ -f "$path/bin/java" ]; then
        FOUND_JAVA17="$path"
        JAVA17_VERSION=$("$path/bin/java" -version 2>&1 | head -n 1)
        echo "✅ Java 17 encontrado: $FOUND_JAVA17"
        echo "   Versão: $JAVA17_VERSION"
        break
    fi
done

if [ -z "$FOUND_JAVA17" ]; then
    echo "❌ Java 17 não encontrado!"
    echo ""
    echo "📦 Para instalar Java 17 SEM alterar Java padrão do sistema:"
    echo "   brew install openjdk@17"
    echo ""
    echo "⚠️  NÃO RODE: brew link openjdk@17"
    echo ""
    echo "Depois rode este script novamente."
    exit 1
fi

# Atualizar settings.json do VS Code
SETTINGS_FILE="/Users/user/mifica/.vscode/settings.json"

if [ ! -f "$SETTINGS_FILE" ]; then
    echo "❌ Arquivo settings.json não encontrado: $SETTINGS_FILE"
    exit 1
fi

echo ""
echo "📝 Atualizando VS Code settings.json..."

# Criar arquivo temporário com a nova configuração
cat > /tmp/vscode_settings.json << EOF
{
    "java.configuration.updateBuildConfiguration": "interactive",
    "java.compile.nullAnalysis.mode": "automatic",
    "java.jdt.ls.java.home": "$FOUND_JAVA17",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-17",
            "path": "$FOUND_JAVA17",
            "default": true
        }
    ],
    "chat.agent.maxRequests": 45
}
EOF

# Substituir o arquivo original
cp /tmp/vscode_settings.json "$SETTINGS_FILE"
rm /tmp/vscode_settings.json

echo "✅ settings.json atualizado com sucesso!"
echo ""
echo "📋 Próximos passos:"
echo "   1. Feche VS Code completamente"
echo "   2. Reabra VS Code"
echo "   3. O aviso sobre Java deve desaparecer"
echo ""
echo "✅ PRONTO! Seu sistema continuará com Java 8 como padrão."
