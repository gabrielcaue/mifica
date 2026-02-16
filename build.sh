#!/bin/bash
set -e

echo "🏗️  Building Mifica Backend..."
cd mifica-backend
mvn clean package -DskipTests -q
echo "✅ Build complete!"
