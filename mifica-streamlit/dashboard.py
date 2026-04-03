import streamlit as st
from PIL import Image
import base64
from io import BytesIO
import requests
from components.user_card import exibir_user_card
from utils.charts import grafico_reputacao
from utils.data import carregar_usuarios

st.set_page_config(page_title="Mifica Dashboard", page_icon="🧠", layout="wide")

st.sidebar.title("🔍 Navegação")
opcao = st.sidebar.radio("Ir para:", ["Dashboard", "Perfil", "Configurações"])

with st.spinner("Carregando dados dos usuários..."):
    usuarios = carregar_usuarios()
    nomes_usuarios = [u["nome"] for u in usuarios]
    usuario_selecionado = st.sidebar.selectbox("Selecionar usuário:", nomes_usuarios)

usuario_logado = st.session_state.get("usuario", {})
role_logado = usuario_logado.get("role", "")


def registrar_transacao_dashboard():
    token = st.session_state.get("token")
    if not token:
        st.info("Faça login para registrar transações.")
        return

    st.subheader("📤 Registrar transação")
    destinatario = st.text_input("Destinatário", key="legacy_dashboard_destinatario")
    valor = st.number_input("Valor (ETH)", min_value=0.0, step=0.01, key="legacy_dashboard_valor")

    if role_logado == "ROLE_ADMIN":
        st.caption("Admin pode transferir para usuários comuns e administradores, sem limite de valor.")
    else:
        st.caption("Usuários comuns só podem transferir para outros usuários comuns.")

    if st.button("Registrar transação", key="legacy_dashboard_registrar_transacao"):
        resposta = requests.post(
            "http://localhost:8080/api/blockchain/transacoes",
            json={"destinatario": destinatario, "valor": valor},
            headers={"Authorization": f"Bearer {token}"},
        )
        if resposta.status_code == 201:
            st.success("Transação registrada com sucesso!")
        else:
            st.error(f"Erro ao registrar transação: {resposta.text}")

st.success("✅ Dados carregados com sucesso!")

# Carregar a imagem
logo = Image.open("assets/logo.png")
buffer = BytesIO()
logo.save(buffer, format="PNG")
img_str = base64.b64encode(buffer.getvalue()).decode()
# Exibir logo + texto lado a lado, centralizados
st.markdown(
    f"""
    <div style="display: flex; align-items: center; justify-content: center;">
        <img src="data:image/png;base64,{img_str}" width="80" style="margin-right:12px;"/>
        <h2 style="margin:0;">Inteligência Modular para Software</h2>
    </div>
    """,
    unsafe_allow_html=True
)

st.markdown("---")


usuario_dados = next((u for u in usuarios if u["nome"] == usuario_selecionado), None)

if opcao == "Dashboard":
    st.subheader(f"📊 Dashboard de {usuario_selecionado}")
    if usuario_dados:
        exibir_user_card(usuario_dados)
    with st.spinner("Gerando gráfico de reputação..."):
        fig = grafico_reputacao(usuarios)
        st.plotly_chart(fig, use_container_width=True)

elif opcao == "Perfil":
    st.subheader(f"👤 Perfil de {usuario_selecionado}")
    st.write("Aqui você pode exibir mais detalhes do perfil futuramente.")

elif opcao == "Configurações":
    st.subheader("⚙️ Configurações")
    st.write("Ajustes e preferências do sistema.")

st.markdown("---")
registrar_transacao_dashboard()
