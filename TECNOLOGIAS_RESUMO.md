# Tecnologias usadas (resumo do resumo)

- **Java 21 + Spring Boot 3:** Backend principal com API REST, regras de negócio e módulos de reputação/gamificação/blockchain.
- **Spring Security + JWT:** Login seguro com autorização por perfil (`USER`/`ADMIN`) e proteção de rotas/endpoints.
- **Maven:** Build, dependências e empacotamento do backend para execução local e produção.
- **JPA/Hibernate + MySQL:** Persistência relacional das entidades do domínio com mapeamento ORM.
- **Redis Pub/Sub (Upstash):** Eventos assíncronos para fluxos de gamificação em tempo quase real.
- **Swagger/OpenAPI:** Documentação automática dos endpoints para consumo e testes da API.
- **React 18 + Vite:** Frontend web SPA com telas de login, cadastro, dashboard e administração.
- **Tailwind CSS + Responsividade Mobile:** Layout mobile-first com ajuste por tela e side menu no celular.
- **React Router DOM:** Controle de navegação, rotas públicas/protegidas e redirecionamento por contexto de acesso.
- **Axios:** Cliente HTTP do frontend para comunicação com a API Java.
- **Python + Streamlit:** Painel analítico complementar com visualizações e integração com backend.
- **Flutter (em evolução):** Base mobile nativa para consumir a mesma API e ampliar demonstração de stack.
- **Docker + Docker Compose:** Padronização de ambiente e orquestração dos serviços em desenvolvimento.
- **Traefik / Nginx / Procfile:** Camada de proxy e suporte de execução/publicação em diferentes ambientes.
- **GitHub Actions (CI/CD):** Pipeline automatizado de build e deploy.
- **Render + GitHub Pages:** Hospedagem de backend e frontend em produção.
- **Spec Kit (local em `tools/spec-kit`):** Toolkit de especificação para apoiar planejamento e evolução do projeto.
- **Git + GitHub:** Versionamento, histórico de mudanças e colaboração.
