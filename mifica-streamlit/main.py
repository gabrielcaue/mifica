import streamlit as st
import mysql.connector
import os

# Configuração inicial da página
st.set_page_config(
    page_title="Painel Administrativo Mifica",
    layout="wide",
    initial_sidebar_state="expanded"
)

# Estilo visual
st.markdown(
    """
    <style>
    section.main > div {
        background-color: #0E1117;
    }
    h1, h2, h3, h4, h5, h6 {
        color: #00C896;
        text-align: center;
    }
    </style>
    """,
    unsafe_allow_html=True
)

# Logotipo
try:
    st.sidebar.image("assets/logo.png", width=120)
except Exception as e:
    st.sidebar.empty()  # Evita renderização flutuante

# Menu lateral padrão (☰)
st.sidebar.title("Menu")
menu = st.sidebar.radio("Navegação", ["Dashboard", "Usuários", "Configurações"])

# Conexão com MySQL
conn = mysql.connector.connect(
    host="localhost",
    user="seu_usuario",
    password="sua_senha",
    database="mifica_db"
)
cursor = conn.cursor()

# Renderização das páginas
if menu == "Dashboard":
    st.title("📊 Dashboard Administrativo")
    cursor.execute("SELECT COUNT(*) FROM usuarios")
    total_usuarios = cursor.fetchone()[0]
    st.metric("Usuários cadastrados", total_usuarios)

elif menu == "Usuários":
    st.title("👥 Gerenciamento de Usuários")
    cursor.execute("SELECT nome, email, role FROM usuarios LIMIT 10")
    rows = cursor.fetchall()
    for nome, email, role in rows:
        st.write(f"**{nome}** — {email} ({role})")

elif menu == "Configurações":
    st.title("⚙️ Configurações do Sistema")
    st.info("Aqui você pode ajustar parâmetros do painel e da aplicação.")

# Fecha conexão
cursor.close()
conn.close()
