# 📊 Mifica Project

Este repositório contém o **Mifica**, uma plataforma que integra backend, frontend e serviços auxiliares para gerenciamento de usuários, transações blockchain e visualização de dados.

---

## 🚀 Tecnologias Utilizadas

- **Backend**: Spring Boot (Java 21, Maven)
- **Frontend**: React + TailwindCSS (build com Node 20 e Nginx)
- **Data Visualization**: Streamlit (Python 3.11)
- **Autenticação**: JWT (Json Web Token)
- **Infraestrutura**: Docker Compose
- **Banco de Dados**: (definir aqui, ex.: PostgreSQL/MySQL)
- **Cloud & Data**: GCP (Google Cloud Platform) e Data Mesh *(em andamento)*

---

## 📂 Estrutura do Projeto

- `backend/` → API REST em Spring Boot
- `frontend/` → Aplicação React (dashboard e telas de login/cadastro)
- `streamlit/` → Visualizações e análises
- `docker-compose.yml` → Orquestração dos serviços
- `docs/` → Documentação adicional

---

## 🔑 Funcionalidades Implementadas

- Cadastro e login de usuários (JWT)
- Cadastro de administradores
- Perfil do usuário autenticado
- Atualização de senha com validação de token
- Estatísticas de usuários
- Integração inicial com blockchain (transações)

---

## ⚠️ Pendências / Próximos Passos

- **Dashboard de Transações**  
  Organizar e estruturar melhor a visualização das transações no frontend.

- **Integração com GCP**  
  Configurar serviços de cloud (armazenamento, autenticação, pipelines).

- **Data Mesh**  
  Definir arquitetura de dados distribuída e integração com os serviços existentes.

---

## 🐳 Como Rodar com Docker

### Subir os serviços
```bash
docker compose up -d
Parar os serviços
bash
docker compose stop
Derrubar tudo (containers + redes)
bash
docker compose down
```
⚡ Dica: use docker compose up -d sem --build para evitar rebuilds pesados.
Use --build apenas quando alterar Dockerfile ou dependências.

## 👨‍💻 Desenvolvimento
Durante desenvolvimento, recomenda-se:

Rodar backend e banco via Docker.

Rodar frontend localmente com:

```bash
cd frontend
npm install
npm start
```
👉 Isso evita builds demorados dentro do Docker.

📌 Contribuição
Faça um fork do projeto

Crie uma branch para sua feature (git checkout -b minha-feature)

Commit suas alterações (git commit -m 'feat: minha nova feature')

Push para a branch (git push origin minha-feature)

Abra um Pull Request

📅 Status
O projeto está em fase de organização e ajustes.
Próximos marcos incluem:

#### Finalizar dashboard de transações

#### Configurar GCP

#### Estruturar Data Mesh
