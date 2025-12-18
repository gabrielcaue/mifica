# db.py
import mysql.connector

def conectar_mysql():
    return mysql.connector.connect(
        host="localhost",
        user="seu_usuario",
        password="sua_senha",
        database="mifica"
    )

def salvar_usuario(nome, email, senha, reputacao=0):
    conn = conectar_mysql()
    cursor = conn.cursor()
    cursor.execute(
        "INSERT INTO usuarios (nome, email, senha, reputacao) VALUES (%s, %s, %s, %s)",
        (nome, email, senha, reputacao)
    )
    conn.commit()
    cursor.close()
    conn.close()

def carregar_usuarios():
    conn = conectar_mysql()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT nome, email, reputacao FROM usuarios")
    usuarios = cursor.fetchall()
    cursor.close()
    conn.close()
    return usuarios

def autenticar_usuario(usuario, senha):
    conn = conectar_mysql()
    cursor = conn.cursor()
    cursor.execute("SELECT senha FROM usuarios WHERE nome=%s", (usuario,))
    result = cursor.fetchone()
    cursor.close()
    conn.close()
    return result and result[0] == senha
