import streamlit as st
from PIL import Image
import requests  # ✅ NOVO: para consumir a API do backend
from components.user_card import exibir_user_card
from utils.charts import grafico_reputacao
from services.blockchain_api import listar_transacoes  # ✅ já estava
from streamlit_autorefresh import st_autorefresh

st.set_page_config(
    page_title="Mifica Dashboard",
    page_icon="🧠",
    layout="wide",
    initial_sidebar_state="expanded"
)

# Atualiza automaticamente a interface a cada 30 minutos
st_autorefresh(interval=30 * 60 * 1000, key="mifica_streamlit_autorefresh_30min")

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
email_logado = usuario_logado.get("email", "")

# ICP-TOTAL: 2
# ICP-01: Dashboard Streamlit calcula saldos por perfil (admin/comum) com base no histórico de transações.


def formatar_moeda(valor):
    return f"R$ {valor:,.2f}".replace(",", "X").replace(".", ",").replace("X", ".")


def calcular_resumo_financeiro(transacoes, email_usuario, role_usuario):
    if not email_usuario:
        return {
            "saldo_usuario_logado": 0.0,
            "saldo_admin_disponivel": 1_000_000.0,
            "saldo_usuarios_comuns": 0.0,
            "total_movimentado": 0.0,
            "entrada_total": 0.0,
            "saida_total": 0.0,
        }

    def saldo_por_email(email):
        entrada = sum(
            float(tx.get("valor", 0) or 0)
            for tx in transacoes
            if tx.get("destinatario") == email
        )
        saida = sum(
            float(tx.get("valor", 0) or 0)
            for tx in transacoes
            if tx.get("remetente") == email
        )
        return entrada, saida, entrada - saida

    entrada_total = sum(
        float(tx.get("valor", 0) or 0)
        for tx in transacoes
        if tx.get("destinatario") == email_usuario
    )
    saida_total = sum(
        float(tx.get("valor", 0) or 0)
        for tx in transacoes
        if tx.get("remetente") == email_usuario
    )

    emails_comuns = {
        u.get("email")
        for u in usuarios
        if u.get("role") == "ROLE_USER" and u.get("email")
    }

    saldo_comuns = 0.0
    for email_comum in emails_comuns:
        _, _, saldo_comum = saldo_por_email(email_comum)
        saldo_comuns += saldo_comum

    saida_admin = saida_total if role_usuario == "ROLE_ADMIN" else 0.0

    return {
        "saldo_usuario_logado": entrada_total - saida_total,
        "saldo_admin_disponivel": max(0.0, 1_000_000.0 - saida_admin),
        "saldo_usuarios_comuns": max(0.0, saldo_comuns),
        "total_movimentado": saida_total if role_usuario == "ROLE_ADMIN" else (entrada_total + saida_total),
        "entrada_total": entrada_total,
        "saida_total": saida_total,
    }


def registrar_transacao_dashboard(saldo_usuario_logado):
    # ICP-02: Após registro bem-sucedido, o rerun garante atualização imediata dos indicadores financeiros.
    token = st.session_state.get("token")
    if not token:
        st.info("Faça login para registrar transações.")
        return

    st.markdown("### 📤 Registrar transação")
    st.caption("O formulário usa apenas destinatário e valor.")
    destinatario = st.text_input("Destinatário", key="dashboard_destinatario")

    if role_logado == "ROLE_ADMIN":
        valor = st.number_input("Valor (ETH)", min_value=0.0, step=0.01, key="dashboard_valor")
        st.caption("Admin pode transferir para usuários comuns e administradores até o limite total de 1.000.000.")
    else:
        limite_disponivel = max(float(saldo_usuario_logado), 0.0)
        valor = st.number_input(
            "Valor (ETH)",
            min_value=0.0,
            max_value=limite_disponivel,
            step=0.01,
            key="dashboard_valor",
        )
        st.caption("Usuários comuns só podem transferir para outros usuários comuns.")

    if st.button("Registrar transação", key="dashboard_registrar_transacao"):
        if role_logado != "ROLE_ADMIN" and valor > max(float(saldo_usuario_logado), 0.0):
            st.error("Saldo insuficiente para registrar esta transação.")
            return

        payload = {"destinatario": destinatario, "valor": valor}
        headers = {"Authorization": f"Bearer {token}"}
        resposta = requests.post("http://localhost:8080/api/blockchain/transacoes", json=payload, headers=headers)

        if resposta.status_code == 201:
            st.success("Transação registrada com sucesso!")
            st.rerun()
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
    resumo_financeiro = calcular_resumo_financeiro(transacoes, email_logado, role_logado)

    if role_logado == "ROLE_ADMIN":
        st.metric("Saldo admin (padrão R$ 1.000.000)", formatar_moeda(resumo_financeiro["saldo_admin_disponivel"]))
        st.metric("Total movimentado", formatar_moeda(resumo_financeiro["total_movimentado"]))
        st.metric("Saldo total usuários comuns", formatar_moeda(resumo_financeiro["saldo_usuarios_comuns"]))
    else:
        st.metric("Seu saldo disponível", formatar_moeda(max(resumo_financeiro["saldo_usuario_logado"], 0.0)))
        st.metric("Total movimentado", formatar_moeda(resumo_financeiro["total_movimentado"]))

    if transacoes:
        for tx in transacoes:
            st.write(f"• {tx['destinatario']} | R$ {tx['valor']}")
    else:
        st.info("Nenhuma transação registrada ainda.")

    st.markdown("---")
    registrar_transacao_dashboard(resumo_financeiro["saldo_usuario_logado"])

elif opcao == "Perfil":
    st.subheader(f"👤 Perfil de {usuario_selecionado}" if usuario_selecionado else "👤 Perfil")
    st.write("Aqui você pode exibir mais detalhes do perfil futuramente.")

elif opcao == "Configurações":
    st.subheader("⚙️ Configurações")
    st.write("Ajustes e preferências do sistema.")
