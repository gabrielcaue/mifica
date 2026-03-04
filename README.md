https://gabrielcaue.github.io/mifica/#/login

# 🧠 Mifica — Plataforma Modular

Eu desenvolvi o Mifica como uma plataforma que integra reputação, gamificação e transações via blockchain, com foco em escalabilidade, segurança e extensibilidade. O projeto é dividido em módulos independentes que se comunicam via API e eventos assíncronos.

---

## 🚀 Funcionalidades principais

✅ Backend em Spring Boot com API REST  
✅ Sistema de reputação e conquistas desbloqueáveis  
✅ Integração com Kafka para eventos assíncronos  
✅ Frontend em React com login, cadastro e dashboard  
✅ Painel administrativo em Streamlit para análises  
✅ Persistência de dados com MySQL  
✅ Autenticação via JWT e controle de acesso por roles  
✅ Documentação automática com Swagger

---

## 📁 Estrutura do Projeto

- `backend/` → # API REST em Spring Boot
- `frontend/` → # Aplicação React
- `streamlit/` → # Painel administrativo e visualizações
- `kafka/` → # Configuração e eventos assíncronos
- `docker-compose.yml` → # Orquestração dos serviços
- `start-dev.sh` → # Script para ambiente local
- `docs/` → # Documentação adicional


---

## 🗄️ Banco de Dados

O projeto utiliza **MySQL** como banco principal.  
Configuração no `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mifica
spring.datasource.username=mifica
spring.datasource.password=mifica123
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

## 🧪 Como rodar localmente

```bash
# Clone o repositório
git clone https://github.com/gabrielcaue/mifica.git

# Acesse o diretório
cd mifica

# Suba os containers
./start-dev.sh
```

O backend ficará disponível em http://localhost:8080, o frontend em http://localhost:5173 e o painel Streamlit em http://localhost:8501.

## 🐳 Deploy com Docker Compose

```bash
# Subir os serviços
docker-compose up --build

# Derrubar os serviços
docker-compose down
```

## 🏗️ Arquitetura

A arquitetura é modular e baseada em microsserviços:

- **Backend (Spring Boot)** → lógica de reputação, gamificação e blockchain
- **Kafka** → eventos assíncronos para comunicação entre módulos
- **Frontend (React)** → interface de usuário
- **Streamlit** → painel administrativo e análises
- **MySQL** → persistência de dados
- **Docker Compose** → orquestração local dos serviços

## 📦 Próximos passos

- 📊 Observabilidade com Prometheus + Grafana  
- 🔐 Autenticação avançada com Keycloak  
- ⚡ Cache distribuído com Redis (ranking em tempo real para gamificação)  
- 🔍 Busca inteligente e analytics com ElasticSearch  
- 🧵 Orquestração com Kubernetes (GKE, EKS, AKS)  
- 🔄 Service Mesh com Istio para controle de tráfego entre microsserviços
- 🌐 CI/CD com GitHub Actions  

### Em manutenção Kubernetes‼️🚨


