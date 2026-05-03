import streamlit as st
from PIL import Image
import requests
import os
from components.user_card import exibir_user_card
from utils.charts import grafico_reputacao
from services.blockchain_api import listar_transacoes
from streamlit_autorefresh import st_autorefresh

st.set_page_config(
    page_title="Mifica Dashboard",
    page_icon="🧠",
    layout="wide",
    initial_sidebar_state="expanded"
)

# Atualiza automaticamente a interface a cada 30 minutos
st_autorefresh(interval=30 * 60 * 1000, key="mifica_streamlit_autorefresh_30min")

# ✅ DEBUG: Verificar se Streamlit está rodando
st.write("✅ Streamlit está rodando corretamente!")
st.write(f"📍 Base path: {st.get_script_run_ctx().session_id if st.get_script_run_ctx() else 'N/A'}")

# ✅ Carregar dados dos usuários
def carregar_usuarios_api():
    try:
        api_url = "http://mifica-backend:8080/api/usuarios"
        response = requests.get(api_url, timeout=5)
        if response.status_code == 200:
            return response.json()
        else:
            st.warning(f"⚠️ API retornou status {response.status_code}")
            return []
    except Exception as e:
        st.info(f"ℹ️ Backend indisponível em dev (normal): {str(e)}")
        return []

usuarios = carregar_usuarios_api()

nomes_usuarios = [u["nome"] for u in usuarios] if usuarios else []
usuario_selecionado = st.sidebar.selectbox("Selecionar usuário:", nomes_usuarios) if nomes_usuarios else None

# Sidebar
st.sidebar.title("🔍 Navegação")
opcao = st.sidebar.radio("Ir para:", ["Dashboard", "Perfil", "Configurações"])

# Logo e título
logo_path = os.path.join(os.path.dirname(__file__), "assets", "logo.png")
if os.path.exists(logo_path):
    try:
        logo = Image.open(logo_path)
        st.image(logo, width=120)
    except Exception as e:
        st.caption(f"Logo error: {str(e)}")
else:
    st.caption("📌 Logo não encontrada")

st.markdown("## Mifica — Inteligência Modular para Software")
st.markdown("---")

# Dados do usuário selecionado
usuario_dados = next((u for u in usuarios if u["nome"] == usuario_selecionado), None)
usuario_logado = st.session_state.get("usuario", {})
role_logado = usuario_logado.get("role", "")
email_logado = usuario_logado.get("email", "")


def formatar_moeda(valor):
    if valor is None or valor == "" or (isinstance(valor, (int, float)) and valor != valor):  # NaN check
        return "R$ 0"
    
    # Converter para inteiro (ignorar centavos)
    valor_inteiro = int(valor)
    
    # Formatar com ponto de milhar usando locale pt-BR
    valor_formatado = f"{valor_inteiro:,}".replace(",", ".")
    
    return f"R$ {valor_formatado}"


def calcular_resumo_financeiro(transacoes, email_usuario, role_usuario):
    if not email_usuario:
        return {
            "valor_disponivel": 0.0,
            "total_movimentado": 0.0,
            "entrada_total": 0.0,
            "saida_total": 0.0,
            "ilimitado": role_usuario == "ROLE_ADMIN",
        }

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

    return {
        "valor_disponivel": entrada_total - saida_total,
        "total_movimentado": entrada_total + saida_total,
        "entrada_total": entrada_total,
        "saida_total": saida_total,
        "ilimitado": role_usuario == "ROLE_ADMIN",
    }


def registrar_transacao_dashboard(valor_disponivel_usuario):
    token = st.session_state.get("token")
    if not token:
        st.info("Faça login para registrar transações.")
        return

    st.markdown("### 📤 Registrar transação")
    st.caption("O formulário usa apenas destinatário e valor.")
    destinatario = st.text_input("Destinatário", key="dashboard_destinatario")

    if role_logado == "ROLE_ADMIN":
        valor = st.number_input("Valor (ETH)", min_value=0.0, step=0.01, key="dashboard_valor")
        st.caption("Admin pode transferir para usuários comuns e administradores com valor ilimitado.")
    else:
        limite_disponivel = max(float(valor_disponivel_usuario), 0.0)
        valor = st.number_input(
            "Valor (ETH)",
            min_value=0.0,
            max_value=limite_disponivel,
            step=0.01,
            key="dashboard_valor",
        )
        st.caption("Usuários comuns só podem transferir para outros usuários comuns.")

    if st.button("Registrar transação", key="dashboard_registrar_transacao"):
        if role_logado != "ROLE_ADMIN" and valor > max(float(valor_disponivel_usuario), 0.0):
            st.error("Saldo insuficiente para registrar esta transação.")
            return

        payload = {"destinatario": destinatario, "valor": valor}
        headers = {"Authorization": f"Bearer {token}"}
    resposta = requests.post("http://mifica-backend:8080/api/transacoes", json=payload, headers=headers)

# Conteúdo condicional
if opcao == "Dashboard":
    st.subheader(f"📊 Dashboard de {usuario_selecionado}" if usuario_selecionado else "📊 Dashboard")

    if usuario_dados:
        exibir_user_card(usuario_dados)

    if usuarios:
        fig = grafico_reputacao(usuarios)
        st.plotly_chart(fig, use_container_width=True)

    transacoes = listar_transacoes()
    resumo_financeiro = calcular_resumo_financeiro(transacoes, email_logado, role_logado)

    st.metric(
        "Valor total disponível",
        "Ilimitado"
        if resumo_financeiro["ilimitado"]
        else formatar_moeda(max(resumo_financeiro["valor_disponivel"], 0.0)),
    )
    st.metric("Total movimentado", formatar_moeda(resumo_financeiro["total_movimentado"]))

    # Histórico de transações visível apenas para administrador
    if role_logado == "ROLE_ADMIN":
        st.markdown("### 🔗 Transações Blockchain")
        if transacoes:
            for tx in transacoes:
                st.write(f"• {tx['remetente']} → {tx['destinatario']} | {formatar_moeda(tx['valor'])} | {tx['dataTransacao']}")
        else:
            st.info("Nenhuma transação registrada ainda.")

        st.markdown("---")
        registrar_transacao_dashboard(resumo_financeiro["valor_disponivel"])

elif opcao == "Perfil":
    st.subheader(f"👤 Perfil de {usuario_selecionado}" if usuario_selecionado else "👤 Perfil")
    st.write("Aqui você pode exibir mais detalhes do perfil futuramente.")

elif opcao == "Configurações":
    st.subheader("⚙️ Configurações")
    st.write("Ajustes e preferências do sistema.")
