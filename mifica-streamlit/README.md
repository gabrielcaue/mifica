# 🧠 Mifica — Painel Administrativo em Streamlit

O **Mifica** é um sistema modular que integra autenticação via JWT, painéis administrativos com **Streamlit**, e uma interface frontend moderna em **React + Vite**.  
Este painel administrativo foi desenvolvido para oferecer uma visão clara e interativa da plataforma, permitindo gestão de reputação, contratos e desafios gamificados.

---

## ⚠️ Status do projeto
**Em andamento** 🚧  
- O painel Streamlit já está funcional e integrado ao frontend.  
- A estilização visual ainda está sendo refinada para garantir uma experiência mais profissional e elegante.  
- O acesso ao painel está restrito a usuários com perfil **admin**.  

---

## 🚀 Tecnologias utilizadas
- **Frontend**: React + TailwindCSS  
- **Backend**: FastAPI + JWT  
- **Painel administrativo**: Streamlit  
- **Banco de dados**: PostgreSQL  
- **Autenticação**: JWT com controle de acesso por role  

---

## 📦 Estrutura do projeto
```bash
mifica/
├── frontend/         # Interface do usuário
├── backend/          # API com autenticação e lógica de negócio
├── streamlit/        # Painel administrativo para admins
└── README.md
```

---

## 🧪 Como rodar localmente

1. **Backend (FastAPI)**
```bash
cd backend/
uvicorn main:app --reload
```
2. **Frontend (React)**
```bash
cd frontend/
npm install
npm run dev
```
3. **Painel administrativo (Streamlit)**
```bash
cd streamlit/
streamlit run main.py
```
## 🔐 Acesso ao painel administrativo
O painel Streamlit é acessível apenas para usuários com perfil admin.

Após login no frontend, o painel é exibido automaticamente em http://localhost:8501.

O menu lateral padrão do Streamlit (☰) está disponível para navegação entre as opções administrativas.

## 📸 Exemplo de tela — Login
<img width="1916" height="891" alt="Login" src="https://github.com/user-attachments/assets/d53f7c78-b5f1-4876-b912-c26c7c0d19f8" />

## 📌 Próximos passos
[ ] Implementar controle de reputação via blockchain

[ ] Criar deploy com Docker ou GCP

[ ] Adicionar testes automatizados

[ ] Refinar estilização visual para maior profissionalismo

## 📫 Contato
👨‍💻 Gabriel — Engenheiro de Software  
 [![Email](https://img.shields.io/badge/Email-red?style=for-the-badge&logo=gmail)](mailto:gabrielcaue3@gmail.com)  [![LinkedIn](https://img.shields.io/badge/LinkedIn-blue?style=for-the-badge&logo=linkedin)](https://www.linkedin.com/in/gabrielcaues)

