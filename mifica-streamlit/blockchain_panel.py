import streamlit as st
import requests

def render():
    st.title("Painel de Transações Blockchain")

    token = st.session_state.get("token")
    if not token:
        st.warning("Faça login para acessar este painel.")
        return

    headers = {"Authorization": f"Bearer {token}"}

    st.subheader("📤 Registrar nova transação")
    hash_tx = st.text_input("Hash da transação")
    remetente = st.text_input("Remetente")
    destinatario = st.text_input("Destinatário")
    valor = st.number_input("Valor (ETH)", min_value=0.0, step=0.01)

    if st.button("Registrar"):
        payload = {
            "hashTransacao": hash_tx,
            "remetente": remetente,
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
            st.markdown(f"**{tx['hashTransacao']}** — {tx['remetente']} ➡️ {tx['destinatario']} ({tx['valor']} ETH) em `{tx['dataTransacao']}`")
    else:
        st.error("Erro ao buscar transações.")

if __name__ == "__main__":
    render()
