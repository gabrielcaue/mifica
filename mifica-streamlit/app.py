import streamlit as st
from PIL import Image
import requests  # ✅ NOVO: para consumir a API do backend
from components.user_card import exibir_user_card
from utils.charts import grafico_reputacao
from services.blockchain_api import listar_transacoes  # ✅ já estava

st.set_page_config(
    page_title="Mifica Dashboard",
    page_icon="🧠",
    layout="wide",
    initial_sidebar_state="expanded"
)

# ✅ NOVO BLOCO: Carregar dados dos usuários direto da API
def carregar_usuarios_api():
    try:
        response = requests.get("http://traefik/api/usuarios")  
        # Se preferir, pode usar "http://backend:8080/api/usuarios" dependendo da rede do docker-compose
        if response.status_code == 200:
            return response.json()
        else:
            st.error(f"Erro ao carregar usuários: {response.status_code}")
            return []
    except Exception as e:
        st.error(f"Erro de conexão com API: {e}")
        return []

usuarios = carregar_usuarios_api()

nomes_usuarios = [u["nome"] for u in usuarios] if usuarios else []
usuario_selecionado = st.sidebar.selectbox("Selecionar usuário:", nomes_usuarios) if nomes_usuarios else None

# Sidebar
st.sidebar.title("🔍 Navegação")
opcao = st.sidebar.radio("Ir para:", ["Dashboard", "Perfil", "Configurações"])

# Logo e título
logo = Image.open("assets/logo.png")
st.image(logo, width=120)
st.markdown("## Mifica — Inteligência Modular para Software")
st.markdown("---")

# Dados do usuário selecionado
usuario_dados = next((u for u in usuarios if u["nome"] == usuario_selecionado), None)

# Conteúdo condicional
if opcao == "Dashboard":
    st.subheader(f"📊 Dashboard de {usuario_selecionado}" if usuario_selecionado else "📊 Dashboard")

    if usuario_dados:
        exibir_user_card(usuario_dados)

    if usuarios:
        fig = grafico_reputacao(usuarios)
        st.plotly_chart(fig, use_container_width=True)

    # ✅ Transações Blockchain
    st.markdown("### 🔗 Transações Blockchain")
    transacoes = listar_transacoes()

    if transacoes:
        for tx in transacoes:
            st.write(f"• {tx['remetente']} → {tx['destinatario']} | R$ {tx['valor']} | {tx['dataTransacao']}")
    else:
        st.info("Nenhuma transação registrada ainda.")

elif opcao == "Perfil":
    st.subheader(f"👤 Perfil de {usuario_selecionado}" if usuario_selecionado else "👤 Perfil")
    st.write("Aqui você pode exibir mais detalhes do perfil futuramente.")

elif opcao == "Configurações":
    st.subheader("⚙️ Configurações")
    st.write("Ajustes e preferências do sistema.")
