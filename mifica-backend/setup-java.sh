#!/bin/bash

# Script para detectar e configurar o JDK do VS Code para Maven

echo "🔍 Procurando JDK do VS Code..."

# Caminho típico do JDK instalado pelo VS Code Extension Pack for Java
VSCODE_JDK_PATHS=(
    "$HOME/.vscode/extensions/vscjava.vscode-java-pack-*/java-pack-jdk"
    "$HOME/.vscode/extensions/*/embedded-jdk"
    "/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home"
    "/usr/libexec/java_home -v 17"
)

FOUND_JDK=""

# Procurar JDK
for path_pattern in "${VSCODE_JDK_PATHS[@]}"; do
    # Expandir wildcards
    for path in $path_pattern; do
        if [ -d "$path" ] && [ -f "$path/bin/java" ]; then
            FOUND_JDK="$path"
            echo "✅ JDK encontrado: $FOUND_JDK"
            break 2
        fi
    done
done

if [ -z "$FOUND_JDK" ]; then
    echo "❌ JDK não encontrado nos caminhos esperados"
    echo ""
    echo "Procurando todos os JDKs instalados..."
    
    if command -v /usr/libexec/java_home &> /dev/null; then
        FOUND_JDK=$(/usr/libexec/java_home -v 17)
        if [ ! -z "$FOUND_JDK" ]; then
            echo "✅ JDK 17 encontrado em: $FOUND_JDK"
        fi
    fi
fi

if [ ! -z "$FOUND_JDK" ]; then
    echo ""
    echo "📝 Configurando Maven..."
    
    # Atualizar arquivo .mvn/jvm.config
    echo "-Djava.home=$FOUND_JDK" > .mvn/jvm.config
    
    # Também exportar a variável
    export JAVA_HOME="$FOUND_JDK"
    export PATH="$FOUND_JDK/bin:$PATH"
    
    echo "✅ Configurado!"
    echo ""
    echo "Testando Java:"
    java -version
    
    echo ""
    echo "🎯 Agora você pode rodar:"
    echo "   mvn clean test"
else
    echo "❌ Nenhum JDK encontrado!"
    echo ""
    echo "Solução: Instale o Extension Pack for Java no VS Code"
    echo "ou instale manualmente um JDK 17 (Temurin, OpenJDK, etc)"
fi
