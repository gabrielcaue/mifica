import streamlit as st
import requests

def render():
    st.title("Painel de Desafios Gamificados")

    token = st.session_state.get("token")
    if not token:
        st.warning("Faça login para acessar este painel.")
        return

    headers = {"Authorization": f"Bearer {token}"}

    st.subheader("🎯 Criar novo desafio")
    titulo = st.text_input("Título do desafio")
    descricao = st.text_area("Descrição")
    pontos = st.number_input("Pontos", min_value=0, step=1)

    if st.button("Criar desafio"):
        payload = {
            "titulo": titulo,
            "descricao": descricao,
            "pontos": pontos
        }
        with st.spinner("Criando desafio..."):
            r = requests.post("http://localhost:8080/api/desafios", json=payload, headers=headers)

        if r.status_code == 201:
            st.toast("Desafio criado com sucesso!", icon="🎯")
            st.success("Desafio criado com sucesso!")
        else:
            st.error(f"Erro: {r.text}")

    st.subheader("📋 Lista de desafios existentes")
    with st.spinner("Carregando desafios..."):
        r = requests.get("http://localhost:8080/api/desafios", headers=headers)

    if r.status_code == 200:
        desafios = r.json()
        st.toast("Desafios carregados!", icon="📋")
        for d in desafios:
            st.markdown(f"**{d['titulo']}** — {d['descricao']} ({d['pontos']} pontos)")
    else:
        st.error("Erro ao buscar desafios.")

if __name__ == "__main__":
    render()
