# 🚀 Guia Completo do Backend Mifica

Bem-vindo ao backend da Mifica! Este guia ajuda você a entender, estender e manter o código.

## 📖 Comece por aqui

### 1️⃣ **Novo no projeto?** (~30 minutos)
👉 [Backend Onboarding Guide](docs/packages/backend-onboarding.md)
- Setup do projeto em 5 minutos
- Arquitetura em 10 minutos
- SOLID e CDD em 5 minutos
- Primeiras tarefas em 5 minutos
- FAQ

### 2️⃣ **Entender a Complexidade** (~20 minutos)
👉 [CDD/ICP Analysis](docs/packages/backend-cdd-analysis.md)
- **ICP**: Índice de Complexidade Percebida de 60 classes
- **Classes Críticas**: UsuarioService (ICP 12-15), UsuarioController (ICP 11-14)
- **Padrões de Refatoração**: Como quebrar classes grandes
- **Roadmap de Melhorias**: 4 fases de refatoração planejadas

### 3️⃣ **Entender os Fluxos** (~25 minutos)
👉 [Workflows & Diagrams](docs/packages/backend-workflows.md)
- 🔐 Fluxo de Autenticação JWT
- 🛡️ Proteção de Requests (JwtFiltro)
- 🎮 Gamificação com Redis Pub/Sub
- ⛓️ Blockchain e Transações
- 🏆 Reputação e Desafios
- ⚠️ Tratamento de Erros

### 4️⃣ **Copiar Padrões** (~15 minutos)
👉 [Code Patterns & Templates](docs/packages/backend-code-patterns.md)
- 📋 Template: Nova Service
- 🎯 Template: Novo Controller
- 🗄️ Template: Nova Entity
- 🔄 Template: Novo Repository
- 📤 Template: Nova DTO
- ✅ Validação e Tratamento de Erros
- 📝 Logging Padronizado

### 5️⃣ **Especificações Detalhadas** (conforme necessário)
- [backend-controller.md](docs/packages/backend-controller.md) - Lista de todos os endpoints
- [backend-service.md](docs/packages/backend-service.md) - Detalhes de cada serviço
- [backend-entity.md](docs/packages/backend-entity.md) - Diagrama de entidades
- [backend-config.md](docs/packages/backend-config.md) - Configuração do projeto
- [backend-repository.md](docs/packages/backend-repository.md) - Acesso a dados

---

## 🗺️ Estrutura do Backend

```
mifica-backend/
├── src/main/java/com/mifica/
│   ├── controller/        # 12+ endpoints REST
│   ├── service/           # 7 serviços de negócio
│   ├── repository/        # 9 repositórios (JPA)
│   ├── entity/            # 9 entidades do banco
│   ├── dto/               # 11 DTOs (request/response)
│   ├── config/            # Security, Redis, Web3
│   ├── filter/            # JWT Filter
│   ├── redis/             # Pub/Sub Gamification
│   ├── blockchain/        # Web3 Integration
│   └── util/              # Helpers (JWT, BCrypt, etc)
├── src/test/java/         # Testes unitários
├── pom.xml               # Maven (Spring Boot 3.5.6, Java 21)
└── README.md             # Instruções locais
```

---

## 🏗️ Stack Tecnológico

| Camada | Tecnologia | Versão |
|--------|-----------|--------|
| **Runtime** | Java | 21 (LTS) |
| **Framework** | Spring Boot | 3.5.6 |
| **Build** | Maven | 3.8+ |
| **Auth** | JWT (jjwt) | 0.11.5 |
| **Persistência** | Spring Data JPA + Hibernate | 6.x |
| **Banco de Dados** | MySQL/PostgreSQL | 8.x/15+ |
| **Cache/Pub-Sub** | Redis (Upstash) | 7+ |
| **Blockchain** | Web3j | 4.x |
| **API Docs** | Swagger/OpenAPI | 2.5.0 |
| **Validação** | Hibernate Validator | Jakarta |

---

## 🎯 Princípios & Padrões

### SOLID
- **S**: Single Responsibility → Cada service tem uma responsabilidade clara
- **O**: Open/Closed → Extensível via patterns (Observer, Strategy)
- **L**: Liskov Substitution → Interfaces bem definidas
- **I**: Interface Segregation → DTOs específicos por endpoint
- **D**: Dependency Inversion → Injeção via Spring @Service/@Repository

### CDD (Cognitive-Driven Development)
- **ICP**: Mede complexidade cognitiva das classes
- **Classes Simples** (ICP 0-3): Entidades, Repositories básicos
- **Classes Moderadas** (ICP 5-7): Services específicos, Controllers simples
- **Classes Críticas** (ICP > 10): UsuarioService, UsuarioController, GamificationSubscriber
- **Ação**: Classes ICP > 10 devem ser refatoradas em fases (veja roadmap)

---

## 🚦 Como Estender

### Adicionar Novo Endpoint
1. Leia o template em [Code Patterns → Controller](docs/packages/backend-code-patterns.md#novo-controller)
2. Crie a DTO de request/response
3. Crie o serviço (ou reutilize existente)
4. Crie o controller com `@PostMapping` / `@GetMapping`
5. Adicione testes unitários em `src/test`
6. Documente no Swagger via `@Operation`

### Adicionar Novo Serviço
1. Leia o template em [Code Patterns → Service](docs/packages/backend-code-patterns.md#novo-service)
2. Defina a responsabilidade única
3. Injete as dependências (Repository, outro Service)
4. Implemente métodos públicos (contrato)
5. Implemente métodos privados (lógica interna)

### Adicionar Nova Entidade
1. Leia o template em [Code Patterns → Entity](docs/packages/backend-code-patterns.md#nova-entity)
2. Crie a classe com `@Entity` e `@Table`
3. Adicione `@Audited` se precisar rastrear mudanças (Envers)
4. Crie o Repository correspondente
5. Crie a DTO correspondente

---

## 🧪 Testes

```bash
# Rodar todos os testes
mvn test

# Rodar teste específico
mvn test -Dtest=UsuarioServiceTest

# Com cobertura
mvn test jacoco:report
```

Ver detalhes em [backend-test.md](docs/packages/backend-test.md)

---

## 🚀 Executar Localmente

```bash
# 1. Clonar repo
git clone https://github.com/seu-usuario/mifica.git
cd mifica

# 2. Setup (Docker Compose)
./start-dev.sh

# 3. Backend começa em http://localhost:8080
# 4. Swagger docs em http://localhost:8080/swagger-ui.html
# 5. Health check em http://localhost:8080/actuator/health
```

Detalhes completos em [Backend Onboarding](docs/packages/backend-onboarding.md#setup)

---

## 🔍 Classes Críticas a Conhecer

| Classe | ICP | Responsabilidade | Arquivo |
|--------|-----|------------------|---------|
| **UsuarioService** | 12-15 | Cadastro, Login, Perfil, Reputação, Créditos | `service/UsuarioService.java` |
| **UsuarioController** | 11-14 | 8+ endpoints (auth, perfil, admin) | `controller/UsuarioController.java` |
| **GamificationSubscriber** | 8-10 | Redis Pub/Sub para eventos gamificação | `redis/GamificationSubscriber.java` |
| **BlockchainService** | 6-8 | Transações blockchain + validação | `blockchain/BlockchainService.java` |
| **SecurityConfig** | 6-7 | JWT + CORS + autorização | `config/SecurityConfig.java` |
| **JwtFiltro** | 5-6 | Intercepta requests e valida tokens | `filter/JwtFiltro.java` |

👉 Veja análise completa em [CDD/ICP Analysis](docs/packages/backend-cdd-analysis.md)

---

## 🐛 Troubleshooting

**P: Maven build falha?**
A: Verifique Java 21 com `java -version`. Se não tiver: `export JAVA_HOME=/path/to/jdk21`

**P: Redis connection refused?**
A: Verifique se `docker-compose up` está rodando: `docker ps | grep redis`

**P: JWT token expirado?**
A: Tokens válidos por 24h. Re-login para novo token.

**P: Qual classe editar para...?**
A: Veja [Workflows & Diagrams](docs/packages/backend-workflows.md) ou FAQ em [Backend Onboarding](docs/packages/backend-onboarding.md#faq)

---

## 📞 Suporte Rápido

- **Dúvidas sobre arquitetura?** → Veja [Workflows](docs/packages/backend-workflows.md)
- **Precisa copiar padrão?** → Veja [Code Patterns](docs/packages/backend-code-patterns.md)
- **Entender complexidade?** → Veja [CDD/ICP Analysis](docs/packages/backend-cdd-analysis.md)
- **Erro específico?** → Veja [Backend Onboarding FAQ](docs/packages/backend-onboarding.md#faq)

---

## 📚 Leitura Adicional

- [README.md](README.md) - Overview do projeto todo
- [TECNOLOGIAS_RESUMO.md](TECNOLOGIAS_RESUMO.md) - Stack tech resumido
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Spring Data JPA Docs](https://spring.io/projects/spring-data-jpa)
- [JWT Handbook](https://auth0.com/resources/ebooks/jwt-handbook)

---

**Última atualização:** Abril 2026 | **Versão Backend:** 1.0.0 | **Java:** 21 | **Spring Boot:** 3.5.6
