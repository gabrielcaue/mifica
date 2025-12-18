# 📚 Documentação Técnica — Mifica Frontend

Este documento complementa o `README.md` com informações técnicas detalhadas sobre a arquitetura, funcionamento e estrutura do projeto **Mifica Frontend**.

---

## 📁 Estrutura de Diretórios

src/
├── assets/           # Imagens, ícones e recursos visuais
├── components/       # Componentes reutilizáveis (botões, formulários, etc.)
├── pages/            # Páginas principais da aplicação (Login, Dashboard, Perfil, etc.)
├── services/         # Configuração de API e chamadas HTTP via Axios
├── context/          # Gerenciamento global de autenticação (AuthContext)
└── App.jsx           # Arquivo principal de rotas e estrutura da aplicação


---

## 🔐 Autenticação e Controle de Acesso

- Autenticação via **JWT**, com token armazenado no `localStorage`
- Decodificação do token com `jwt-decode` para extrair dados do usuário
- Gerenciamento de sessão e permissões via `AuthContext`
- Redirecionamento inteligente após login usando `React Router DOM` e `location.state`
- Rotas protegidas verificam autenticação e redirecionam para `/login` com retorno à origem

---

## 🔄 Comunicação com o Backend

- Requisições HTTP feitas com **Axios**, configurado em `services/api.js`
- Backend esperado em `http://localhost:8080`
- Endpoints utilizados:
  - `POST /usuarios/login`
  - `POST /usuarios/cadastro`
  - `POST /admin/acesso`
- Estrutura preparada para expansão com novos endpoints REST

---

## 🧠 Gerenciamento de Estado

- Estado global de autenticação mantido via `AuthContext`
- Dados do usuário persistidos no `localStorage` para manter sessão após reload
- Estado `carregando` controla a renderização da aplicação até validação do token

---

## 🧩 Modularidade e Escalabilidade

- Componentes desacoplados e reutilizáveis
- Separação clara entre lógica, visual e dados
- Facilidade para adicionar novas páginas, rotas e integrações
- Arquitetura preparada para integração com serviços externos e persistência

---

## ⚠️ Considerações sobre Persistência e Segurança

A funcionalidade de **alteração de senhas** requer integração com um sistema de banco de dados relacional — como **MySQL**, **PostgreSQL**, ou outro de preferência — para garantir a persistência segura das credenciais e permitir operações de atualização autenticadas.

A implementação dessa camada foi deixada em aberto, podendo ser definida conforme a necessidade do recrutador, consumidor ou equipe técnica responsável pela evolução do projeto.

---

## 🚀 Próximos Passos

O projeto está preparado para receber as seguintes integrações:

- Visualização analítica com **Streamlit**
- Containerização com **Docker**
- Deploy em **Google Cloud Platform (GCP)**
- Conexão com **Data Mash** para enriquecimento de dados
- Integração opcional com banco de dados para persistência de usuários e senhas

---

> A arquitetura foi desenvolvida com foco em modularidade e escalabilidade, permitindo adaptações conforme o contexto de uso.
