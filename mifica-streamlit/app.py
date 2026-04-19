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
usuario_logado = st.session_state.get("usuario", {})
role_logado = usuario_logado.get("role", "")


def registrar_transacao_dashboard():
    token = st.session_state.get("token")
    if not token:
        st.info("Faça login para registrar transações.")
        return

    st.markdown("### 📤 Registrar transação")
    st.caption("O formulário usa apenas destinatário e valor.")
    destinatario = st.text_input("Destinatário", key="dashboard_destinatario")
    valor = st.number_input("Valor (ETH)", min_value=0.0, step=0.01, key="dashboard_valor")

    if role_logado == "ROLE_ADMIN":
        st.caption("Admin pode transferir para usuários comuns e administradores até o limite total de 1.000.000.")
    else:
        st.caption("Usuários comuns só podem transferir para outros usuários comuns.")

    if st.button("Registrar transação", key="dashboard_registrar_transacao"):
        payload = {"destinatario": destinatario, "valor": valor}
        headers = {"Authorization": f"Bearer {token}"}
        resposta = requests.post("http://localhost:8080/api/blockchain/transacoes", json=payload, headers=headers)

        if resposta.status_code == 201:
            st.success("Transação registrada com sucesso!")
        else:
            st.error(f"Erro ao registrar transação: {resposta.text}")

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
            st.write(f"• {tx['destinatario']} | R$ {tx['valor']} | {tx['dataTransacao']}")
    else:
        st.info("Nenhuma transação registrada ainda.")

    st.markdown("---")
    registrar_transacao_dashboard()

elif opcao == "Perfil":
    st.subheader(f"👤 Perfil de {usuario_selecionado}" if usuario_selecionado else "👤 Perfil")
    st.write("Aqui você pode exibir mais detalhes do perfil futuramente.")

elif opcao == "Configurações":
    st.subheader("⚙️ Configurações")
    st.write("Ajustes e preferências do sistema.")
