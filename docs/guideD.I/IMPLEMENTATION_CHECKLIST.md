# 🎯 Checklist: Implementação de Testes

## Fase 1: Setup (1-2 horas)

- [ ] **Ler documentação**
  - [ ] Ler [TESTING_QUICK_REFERENCE.md](../TESTING_QUICK_REFERENCE.md) (5 min)
  - [ ] Ler [TESTING_STRATEGY.md](../TESTING_STRATEGY.md) (15 min)
  - [ ] Entender pirâmide de testes

- [ ] **Configurar pom.xml**
  - [ ] Adicionar Mockito
  - [ ] Adicionar AssertJ
  - [ ] Adicionar Testcontainers
  - [ ] Adicionar JaCoCo
  - [ ] Executar `mvn clean install`

- [ ] **Criar application-test.yml**
  - [ ] Criar `src/test/resources/application-test.yml`
  - [ ] Configurar H2 em memória
  - [ ] Configurar JPA com create-drop

- [ ] **Verificar setup**
  - [ ] Executar `mvn clean test`
  - [ ] Verificar que testes da comunidade carregam
  - [ ] Gerar cobertura: `mvn clean test jacoco:report`

---

## Fase 2: Testes Unitários (3-4 dias)

### Dia 1: GamificationService

- [ ] **Estudar exemplo**
  - [ ] Abrir [GamificationServiceUnitTest.java](../../mifica-backend/src/test/java/com/mifica/service/GamificationServiceUnitTest.java)
  - [ ] Entender cada @Test
  - [ ] Entender uso de Mockito

- [ ] **Implementar testes**
  - [ ] Copiar estrutura do exemplo
  - [ ] Escrever 5+ testes para GamificationService
  - [ ] Executar: `mvn test -Dtest=GamificationServiceUnitTest`
  - [ ] Verificar que todos passam ✅

- [ ] **Validade cobertura**
  - [ ] Atingir 80%+ de cobertura
  - [ ] Revisar branches não testadas

### Dia 2: UsuarioService

- [ ] **Estudar exemplo**
  - [ ] Abrir [UsuarioServiceUnitTest.java](../../mifica-backend/src/test/java/com/mifica/service/UsuarioServiceUnitTest.java)
  - [ ] Entender testes de autenticação
  - [ ] Entender testes de validação

- [ ] **Implementar testes**
  - [ ] Escrever testes de criar usuário
  - [ ] Escrever testes de validação de login
  - [ ] Escrever testes de mudança de senha
  - [ ] Escrever testes de reputação
  - [ ] Executar: `mvn test -Dtest=UsuarioServiceUnitTest`

### Dia 3: Outros Services

- [ ] **ReputacaoService**
  - [ ] Escrever testes unitários
  
- [ ] **DesafioService**
  - [ ] Escrever testes unitários

- [ ] **ContratoService**
  - [ ] Escrever testes unitários

- [ ] **Validar total**
  - [ ] Executar: `mvn clean test -Dtest=*Service*Unit*`
  - [ ] Verificar cobertura ≥ 80%

### Dia 4: Utilitários e Helpers

- [ ] **Testes para Utils**
  - [ ] Identificar classes Util importantes
  - [ ] Escrever testes para DateUtil, ValidationUtil, etc.

- [ ] **Testes para Filters**
  - [ ] Escrever testes para filters

---

## Fase 3: Testes de Integração (2-3 dias)

### Dia 1: GamificationController

- [ ] **Estudar exemplo**
  - [ ] Abrir [GamificationControllerIntegrationTest.java](../../mifica-backend/src/test/java/com/mifica/controller/GamificationControllerIntegrationTest.java)
  - [ ] Entender @SpringBootTest + H2
  - [ ] Entender MockMvc

- [ ] **Implementar testes**
  - [ ] Escrever teste POST /add-points
  - [ ] Escrever teste GET /user/{id}
  - [ ] Escrever teste com dados inválidos
  - [ ] Executar: `mvn test -Dtest=GamificationControllerIntegrationTest`

### Dia 2: Outros Controllers

- [ ] **UsuarioController**
  - [ ] Escrever teste de login
  - [ ] Escrever teste de criar usuário
  - [ ] Escrever teste de autorização

- [ ] **ContratoController**
  - [ ] Escrever testes de CRUD

- [ ] **DesafioController**
  - [ ] Escrever testes de listagem

### Dia 3: Redis Pub/Sub (Opcional)

- [ ] **Estudar GamificationRedisIntegrationTest existente**
  - [ ] Entender como testa Redis

- [ ] **Melhorar testes**
  - [ ] Adicionar mais cenários
  - [ ] Adicionar timeout handling

---

## Fase 4: CI/CD Integration (1 dia)

- [ ] **Criar GitHub Actions workflow**
  - [ ] Criar `.github/workflows/test.yml`
  - [ ] Configurar para rodar `mvn clean test`
  - [ ] Configurar para gerar cobertura
  
- [ ] **Adicionar badges**
  - [ ] Badge de build status
  - [ ] Badge de cobertura
  - [ ] Adicionar ao README.md

- [ ] **Configurar branch protection**
  - [ ] Require testes passarem antes de merge
  - [ ] Require cobertura mínima

---

## Fase 5: Monitoramento e Manutenção (Contínuo)

- [ ] **Manter cobertura**
  - [ ] Revisar cobertura mensalmente
  - [ ] Meta: 80%+

- [ ] **Novos testes para bugs**
  - [ ] Sempre que encontrar bug: escrever teste first
  - [ ] Depois corrigir código
  - [ ] Assim bug nunca volta

- [ ] **Refatorar testes**
  - [ ] Extrair builders reutilizáveis
  - [ ] Criar base test classes

---

## Métricas para Acompanhar

### ✅ Deve Verificar Regularmente

- [ ] **Cobertura de código**
  - [ ] Target: 80%+
  - [ ] Comando: `mvn clean test jacoco:report`
  - [ ] Arquivo: `target/site/jacoco/index.html`

- [ ] **Tempo de testes**
  - [ ] Target: < 60 segundos no total
  - [ ] Unitários: < 30s
  - [ ] Integração: < 30s

- [ ] **Taxa de falha**
  - [ ] Target: 0% falsos positivos
  - [ ] Testes devem ser determinísticos

- [ ] **Cobertura por pacote**
  ```
  src/main/java/com/mifica/
  ├── service/      → 90%+
  ├── controller/   → 85%+
  ├── entity/       → 70% (menos crítico)
  ├── repository/   → 80%+ (mockados)
  ├── util/         → 85%+
  └── filter/       → 80%+
  ```

---

## Problemas Comuns e Soluções

### ❌ "Testes falhando aleatoriamente"
- [ ] Verificar se há compartilhamento de dados entre testes
- [ ] Adicionar `@DirtiesContext` se necessário
- [ ] Limpar dados em `@AfterEach`

### ❌ "Testes muito lentos"
- [ ] Verificar se está usando @SpringBootTest em testes unitários
- [ ] Mover para MockitoExtension
- [ ] Usar Fake em vez de Real Repository

### ❌ "Não consigo mockar classe final"
- [ ] Adicionar dependência: `mockito-inline`
- [ ] Ou refatorar código para não ser final

### ❌ "NoSuchMethodError no teste"
- [ ] Executar `mvn clean install` completo
- [ ] Verificar versões de dependências

---

## Recursos Úteis

- 📖 [Mockito - JavaDoc](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- 📖 [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- 📖 [AssertJ Documentation](https://assertj.github.io/assertj-core-features-highlight.html)
- 📖 [Test Pyramid - Martin Fowler](https://martinfowler.com/bliki/TestPyramid.html)

---

## Status Final

### ✅ Quando Estiver Pronto

```
✅ 70-80% cobertura de código
✅ Testes rodam em < 1 minuto
✅ Testes rodam em CI/CD automaticamente
✅ 0% falsos positivos
✅ Documentação atualizada
✅ Time confortável escrevendo testes
✅ Novos PRs SEMPRE têm testes
```

### 🎉 Resultado

> Você terá uma **suíte de testes confiável, rápida e mantível** que garante a qualidade do projeto!

---

## Próxima Ação

👉 **Comece agora:**

```bash
# 1. Abra este arquivo
cat IMPLEMENTATION_CHECKLIST.md

# 2. Comece com Fase 1
# 3. Marque cada item conforme completa
# 4. Commit mensagens: "test: add GamificationService tests"
# 5. PR com testes novos
```

---

**Boa sorte! 🚀**

Qualquer dúvida, releia:
- [TESTING_QUICK_REFERENCE.md](../TESTING_QUICK_REFERENCE.md) - Rápido (5 min)
- [TESTING_STRATEGY.md](../TESTING_STRATEGY.md) - Detalhado (20 min)
