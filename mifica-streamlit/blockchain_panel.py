import streamlit as st
import requests

def render():
    st.title("Painel de Transações Blockchain")

    token = st.session_state.get("token")
    usuario = st.session_state.get("usuario", {})
    role = usuario.get("role", "")
    if not token:
        st.warning("Faça login para acessar este painel.")
        return

    headers = {"Authorization": f"Bearer {token}"}

    st.subheader("📤 Registrar nova transação")
    destinatario = st.text_input("Destinatário")
    valor = st.number_input("Valor (ETH)", min_value=0.0, step=0.01)

    if role == "ROLE_ADMIN":
        st.caption("Admin pode transferir para usuários comuns e administradores até o limite total de 1.000.000.")
    else:
        st.caption("Usuários comuns só podem transferir para outros usuários comuns.")

    if st.button("Registrar"):
        payload = {
            "destinatario": destinatario,
            "valor": valor
        }
        r = requests.post("http://localhost:8080/api/blockchain/transacoes", json=payload, headers=headers)
        if r.status_code == 201:
            st.success("Transação registrada com sucesso!")
        else:
            st.error(f"Erro: {r.text}")

    st.subheader("📋 Histórico de transações")
    r = requests.get("http://localhost:8080/api/blockchain/transacoes", headers=headers)
    if r.status_code == 200:
        transacoes = r.json()
        for tx in transacoes:
            st.markdown(f"{tx['destinatario']} ({tx['valor']} ETH) em `{tx['dataTransacao']}`")
    else:
        st.error("Erro ao buscar transações.")

if __name__ == "__main__":
    render()
