import json
import os

def carregar_usuarios(path="data/usuarios.json"):
    """
    Carrega os dados dos usuários a partir de um arquivo JSON.

    Parâmetros:
        path (str): Caminho para o arquivo JSON contendo os usuários.

    Retorna:
        list: Lista de dicionários com os dados dos usuários.
    """
    if not os.path.exists(path):
        raise FileNotFoundError(f"Arquivo não encontrado: {path}")

    with open(path, encoding="utf-8") as f:
        return json.load(f)
