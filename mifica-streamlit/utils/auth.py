# auth.py
from db import autenticar_usuario, salvar_usuario

def login(usuario, senha):
    """
    Faz a autenticação de um usuário no banco MySQL.
    Retorna True se a senha estiver correta, False caso contrário.
    """
    return autenticar_usuario(usuario, senha)

def registrar_usuario(nome, email, senha):
    """
    Registra um novo usuário no banco MySQL.
    """
    salvar_usuario(nome, email, senha)
