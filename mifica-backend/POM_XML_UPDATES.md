# Adicionar ao pom.xml

## Seção a Adicionar no `<dependencies>`

Adicione estas dependências ao seu `pom.xml` (procure a seção `<!-- Testes com JUnit 5 -->` e adicione APÓS ela):

```xml
        <!-- Testes com JUnit 5 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- 🆕 ADICIONAR ESTAS LINHAS 👇 -->

        <!-- Mockito para criar mocks em testes unitários -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Integração do Mockito com JUnit 5 -->
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- AssertJ para assertions mais legíveis (opcional mas recomendado) -->
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Testcontainers para Redis (opcional, use depois) -->
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers</artifactId>
            <version>1.19.7</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>1.19.7</version>
            <scope>test</scope>
        </dependency>

        <!-- 🆕 FIM DA ADIÇÃO 👆 -->

        <dependency>
            <groupId>org.hibernate.validator</groupId>
            <artifactId>hibernate-validator</artifactId>
             <version>8.0.1.Final</version>
        </dependency>
```

---

## Seção a Adicionar no `<build><plugins>`

Adicione o plugin JaCoCo para cobertura de testes (OPCIONAL mas recomendado):

```xml
        <plugins>
            <!-- Plugin Spring Boot -->
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>

            <!-- 🆕 ADICIONAR JaCoCo para cobertura 👇 -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.10</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <!-- 🆕 FIM DA ADIÇÃO JaCoCo 👆 -->

        </plugins>
```

---

## Arquivo application-test.yml

Crie um novo arquivo em `mifica-backend/src/test/resources/application-test.yml`:

```yaml
spring:
  profiles:
    active: test
  
  # ✅ H2 Em Memória (NÃO USA MYSQL)
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false
    driver-class-name: org.h2.Driver
    username: sa
    password: 

  # ✅ JPA com H2
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true
        show_sql: false
        dialect: org.hibernate.dialect.H2Dialect

  # ✅ Redis Mock (comentado, ativa se precisar Testcontainers)
  redis:
    host: localhost
    port: 6379
    timeout: 60000ms

# ✅ Logs para debug
logging:
  level:
    com.mifica: DEBUG
    org.springframework.test: DEBUG
    org.hibernate.SQL: DEBUG
    root: INFO
```

---

## Como Adicionar (Step by Step)

### 1️⃣ Abra seu pom.xml

```bash
code /Users/user/mifica/mifica-backend/pom.xml
```

### 2️⃣ Localize a linha com "Testes com JUnit 5"

```xml
<!-- Testes com JUnit 5 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### 3️⃣ Cole as novas dependências APÓS esta seção

### 4️⃣ Salve e execute

```bash
cd /Users/user/mifica/mifica-backend
mvn clean install
```

### 5️⃣ Crie application-test.yml

```bash
mkdir -p src/test/resources
cat > src/test/resources/application-test.yml << 'EOF'
spring:
  profiles:
    active: test
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop

logging:
  level:
    com.mifica: DEBUG
EOF
```

---

## Verificar Instalação

```bash
cd /Users/user/mifica/mifica-backend

# Tentar compilar
mvn clean compile

# Rodar testes
mvn clean test

# Com cobertura
mvn clean test jacoco:report

# Abrir relatório (macOS)
open target/site/jacoco/index.html
```

---

## Próximos Passos

✅ Adicionar Mockito + Testcontainers ao pom.xml
✅ Criar application-test.yml
✅ Implementar testes unitários dos Services
✅ Implementar testes de integração dos Controllers
✅ Configurar CI/CD para rodar testes antes de merge
