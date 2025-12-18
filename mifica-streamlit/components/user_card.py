import streamlit as st

def exibir_user_card(usuario):
    st.markdown(f"### 👤 {usuario['nome']}")
    st.metric(label="Reputação", value=usuario["reputacao"])
    st.write("**Conquistas:**")
    for conquista in usuario["conquistas"]:
        st.markdown(f"- {conquista}")
