import streamlit as st
import requests
import jwt

def render():
    # Estilo visual completo
    st.markdown(
    """
    <style>
    div.login-box {
        background-color: #1C1F26;
        padding: 2rem;
        border-radius: 12px;
        box-shadow: 0 0 10px rgba(0,0,0,0.3);
        max-width: 400px;
        margin: 3rem auto;
        color: #FAFAFA;
        display: flex;
        flex-direction: column;
        align-items: stretch;
    }
    div.login-box:empty {
        display: none; /* 🔧 remove blocos vazios */
    }
    </style>
    """,
    unsafe_allow_html=True
)


    # Caixa de login
    with st.container():
        st.markdown('<div class="login-box">', unsafe_allow_html=True)
        st.markdown("<h3>🔐 Painel de Autenticação</h3>", unsafe_allow_html=True)

        email = st.text_input("Email", placeholder="Digite seu email")
        senha = st.text_input("Senha", type="password", placeholder="Digite sua senha")

        if st.button("Entrar"):
            with st.spinner("Autenticando..."):
                r = requests.post("http://localhost:8080/api/auth/login", json={"email": email, "senha": senha})

            if r.status_code == 200:
                token = r.json()["token"]
                st.session_state["token"] = token

                decoded = jwt.decode(token, options={"verify_signature": False})
                st.session_state["usuario"] = {
                    "nome": decoded.get("nome", "Usuário"),
                    "email": decoded.get("email", decoded.get("sub", "")),
                    "role": decoded.get("role", "")
                }

                st.success("✅ Login realizado com sucesso!")
                st.text_area("Token JWT", token[:10] + "...", height=100)
                st.markdown(f"👤 **Usuário:** {st.session_state['usuario']['nome']}")
                st.markdown(f"📧 **Email:** {st.session_state['usuario']['email']}")
            else:
                st.error("Credenciais inválidas ou erro no servidor.")

                st.markdown("</div>", unsafe_allow_html=True)

    # Caixa de cadastro de administrador
    with st.container():
        st.markdown('<div class="login-box">', unsafe_allow_html=True)
        st.markdown("<h3>🛡️ Cadastro de Administrador</h3>", unsafe_allow_html=True)

        nome = st.text_input("Nome completo")
        email_admin = st.text_input("Email para cadastro")
        senha_admin = st.text_input("Senha de acesso", type="password")
        telefone = st.text_input("Telefone")
        data_nascimento = st.date_input("Data de nascimento").strftime("%Y-%m-%d")
        senha_acesso = st.text_input("Senha especial para cadastro", type="password")

        if st.button("Cadastrar Administrador"):
            payload = {
                "nome": nome,
                "email": email_admin,
                "senha": senha_admin,
                "telefone": telefone,
                "dataNascimento": data_nascimento,
                "role": "ADMIN",
                "senhaAcesso": senha_acesso
            }

            try:
                r = requests.post("http://localhost:8080/api/usuarios/cadastro-admin", json=payload)

                if r.status_code == 200:
                    st.success("Administrador cadastrado com sucesso!")
                elif r.status_code == 403:
                    st.error(f"Erro 403: {r.text}")
                else:
                    st.error(f"Erro {r.status_code}: {r.text}")
            except Exception as e:
                st.error(f"Erro de conexão: {e}")

        st.markdown("</div>", unsafe_allow_html=True)

if __name__ == "__main__":
    render()
