# 🌐 Mifica Frontend

Interface web do projeto **Mifica**, desenvolvida em **React + Vite**, integrada ao backend em **Spring Boot + MySQL** e ao painel administrativo em **Streamlit**.  
O objetivo é oferecer uma plataforma modular com reputação, gamificação e transações blockchain.

---

## 🚀 Demonstração 

### Telas principais
- **Cadastro de Usuário**  
- **Login**  
- **Perfil**  
- **Dashboard Administrativo (Streamlit)**  

---

## 🛠️ Tecnologias utilizadas

- **Frontend**: React + Vite, TailwindCSS, Axios, React Router DOM  
- **Backend**: Spring Boot, JWT Authentication, Swagger  
- **Banco de Dados**: MySQL (persistência completa de usuários e reputação)  
- **Admin Panel**: Streamlit (embutido no frontend via iframe)  
- **Infraestrutura**: Docker (backend já configurado, frontend em andamento)  

---

## 📦 Funcionalidades

- Cadastro de usuários e administradores com persistência no MySQL  
- Autenticação JWT e proteção de rotas  
- Sistema de reputação e conquistas desbloqueáveis  
- Painel administrativo integrado com Streamlit (menu lateral padrão)  
- Comunicação com backend via API REST  
- Estrutura modular e escalável  

---

## ⚙️ Como rodar localmente

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/frontend-mifica.git
cd frontend-mifica

# Instale dependências
npm install

# Rode o frontend
npm run dev
```
Certifique-se de que:

O backend esteja rodando em http://localhost:8080

O Streamlit esteja rodando em http://localhost:8501

O MySQL esteja ativo e com a tabela usuarios criada:
```bash
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100),
    senha VARCHAR(100) NOT NULL,
    reputacao INT DEFAULT 0
);
```
## 🧩 Próximos passos
### ☁️ Deploy em Google Cloud Platform (GCP) com Cloud Run e Cloud SQL

### 🔗 Integração com Data Mash para enriquecimento de dados

### 📊 CI/CD com GitHub Actions para automação de testes e deploy

## 👨‍💻 Autor
Desenvolvido por Gabriel Cauê
## 📫 LinkedIn
[![LinkedIn](https://img.shields.io/badge/LinkedIn-blue?style=for-the-badge&logo=linkedin)](https://www.linkedin.com/in/gabrielcaues)
