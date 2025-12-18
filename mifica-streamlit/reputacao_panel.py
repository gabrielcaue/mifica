import streamlit as st
import requests

def render():
    st.title("Painel de Reputação")

    token = st.session_state.get("token")
    if not token:
        st.warning("Faça login para acessar este painel.")
        return

    email = st.text_input("Email do usuário para consulta")
    if email:
        headers = {"Authorization": f"Bearer {token}"}
        with st.spinner("Consultando histórico de reputação..."):
            r = requests.get(f"http://localhost:8080/api/reputacao?email={email}", headers=headers)

        if r.status_code == 200:
            historico = r.json()
            st.toast("Histórico carregado com sucesso!", icon="📋")
            st.subheader("📋 Histórico de reputação")
            for h in historico:
                st.markdown(f"**{h['emailUsuario']}** — {h['reputacaoAnterior']} ➡️ {h['reputacaoNova']} em `{h['dataAlteracao']}`")
        else:
            st.error("Erro ao buscar histórico.")

if __name__ == "__main__":
    render()
