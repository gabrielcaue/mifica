import streamlit as st
import requests

st.title("Painel de Contratos Inteligentes")

token = st.text_input("Token JWT", type="password")

if token:
    headers = {"Authorization": f"Bearer {token}"}

    st.subheader("📄 Criar novo contrato")
    nome = st.text_input("Nome do contrato")
    descricao = st.text_area("Descrição")
    endereco = st.text_input("Endereço Blockchain")

    if st.button("Criar contrato"):
        payload = {
            "nome": nome,
            "descricao": descricao,
            "enderecoBlockchain": endereco
        }
        r = requests.post("http://localhost:8080/api/contratos", json=payload, headers=headers)
        if r.status_code == 201:
            st.success("Contrato criado com sucesso!")
        else:
            st.error(f"Erro: {r.text}")

    st.subheader("📋 Lista de contratos existentes")
    r = requests.get("http://localhost:8080/api/contratos", headers=headers)
    if r.status_code == 200:
        contratos = r.json()
        for c in contratos:
            st.markdown(f"**{c['nome']}** — {c['descricao']} (`{c['enderecoBlockchain']}`)")
    else:
        st.error("Erro ao buscar contratos.")
