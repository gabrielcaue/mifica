# 🔧 Guia de Execução - Testes & Deploy

## 📋 Contexto

Foram identificados e corrigidos 19 erros em testes de CI/CD que impediam o deployment automático.

**Documentação de referência**:
- [TEST_FAILURES_ANALYSIS.md](./TEST_FAILURES_ANALYSIS.md) — Análise técnica profunda
- [CORRECOES_TESTES_RESUMO.md](./CORRECOES_TESTES_RESUMO.md) — Resumo executivo

---

## ✅ Verificação Local Antes do Deploy

### 1. Rodar Testes Localmente
```bash
cd mifica-backend

# Rodar TODOS os testes
mvn clean test

# Rodar APENAS testes unitários (rápido)
mvn clean test -Dtest="*Test" -DskipITs

# Rodar com debug
mvn clean test -X

# Rodar teste específico
mvn clean test -Dtest=UsuarioControllerIntegrationTest#testCadastrarUsuario_Success
```

### 2. Verificar Build
```bash
# Build sem testes (útil para debug)
mvn clean install -DskipTests

# Build completo com testes e SonarQube
mvn clean install sonar:sonar \
  -Dsonar.projectKey=mifica-backend \
  -Dsonar.sources=src/main \
  -Dsonar.tests=src/test
```

### 3. Verificar Cobertura de Testes
```bash
# Gerar relatório de cobertura
mvn clean test jacoco:report

# Abrir relatório (macOS)
open target/site/jacoco/index.html

# Ou (Linux)
firefox target/site/jacoco/index.html
```

---

## 🚀 Deploy Automático

### Método 1: Usar Script (Recomendado)
```bash
# Na raiz do projeto
./deploy-prod.sh
```

**O que o script faz**:
1. Detecta mudanças com `git diff`
2. Gera mensagem de commit automática
3. Faz `git add` + `git commit` + `git push origin master`
4. GitHub Actions dispara pipeline:
   - ✅ Maven: `mvn clean test`
   - ✅ SonarQube: análise de qualidade
   - ✅ Railway: deployment automático

### Método 2: Manual (se preferir)
```bash
# 1. Fazer mudanças e testar
mvn clean test

# 2. Se tudo passar, fazer commit
git add -A
git commit -m "chore(backend): update service"

# 3. Fazer push
git push origin master

# 4. Monitorar em GitHub Actions
# https://github.com/gabrielcaue/mifica/actions
```

---

## 🔍 Troubleshooting

### ❌ Erro: "ApplicationContext failure threshold exceeded"

**Solução**: Já foi corrigido em `application-test.yml`

Se ainda vir esse erro:
1. Limpar Maven cache: `mvn clean`
2. Verificar Java version: `java -version` (deve ser 17+)
3. Rodar com debug: `mvn clean test -X 2>&1 | grep -A5 "ApplicationContext"`

### ❌ Erro: "NullPointerException" em GamificationServiceFakeTest

**Solução**: Já foi corrigido adicionando `assertThat(user.getId()).isNotNull()`

Se ainda vir esse erro:
1. Verificar se `FakeUserRepository.save()` está retornando entity com ID
2. Rodar teste isolado: `mvn test -Dtest=GamificationServiceFakeTest`

### ❌ GitHub Actions falha mas local passa

**Verificar**:
```bash
# Rodar com Java 17 (mesmo que CI)
export JAVA_HOME=/usr/libexec/java_home -v 17
mvn clean test

# Ou especificar versão
mvn clean test -java.version=17
```

---

## 📊 Status Esperado Após Correções

```
[INFO] Tests run: 37, Failures: 0, Errors: 0, Skipped: 1, Time elapsed: 28 s
[INFO] BUILD SUCCESS
```

**Breakdown**:
- 31 testes unitários ✅
- 5 testes de integração ✅
- 1 teste skipped (Redis não disponível, aceitável)

---

## 📈 Monitoramento Contínuo

### GitHub Actions Dashboard
```
https://github.com/gabrielcaue/mifica/actions
```

### Logs de Deploy
```bash
# Ver últimas ações
git log --oneline | head -5

# Ver status
git status
```

### Railway Dashboard
```
https://railway.app/project/[YOUR_PROJECT_ID]
```

---

## 🎯 Próximos Passos (Roadmap)

### Imediato (Hoje)
- [x] Corrigir testes de integração
- [x] Corrigir testes fake
- [ ] **Testar localmente e fazer push**
- [ ] Monitorar CI/CD passar

### Curto Prazo (Esta semana)
- [ ] Adicionar Testcontainers para Redis
- [ ] Aumentar cobertura para 70%
- [ ] Adicionar testes de performance

### Médio Prazo (Próximas 2-4 semanas)
- [ ] Migrar para Spring Boot 3.5+
- [ ] Adicionar GraalVM native image
- [ ] Setup de SonarQube quality gate

---

## 📝 Checklist Pre-Deploy

- [ ] Testes passam localmente: `mvn clean test`
- [ ] Build sem erro: `mvn clean install`
- [ ] Cobertura aceitável: `mvn jacoco:report`
- [ ] Nenhum warning de security
- [ ] Commit message descritivo
- [ ] Branch está atualizado: `git pull origin master`

---

## 💬 Dúvidas?

**Documentação**:
- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [Projeto Mifica - Documentação](./README.md)
- [Technology Integration Map](./TECHNOLOGY_INTEGRATION_MAP.md)

