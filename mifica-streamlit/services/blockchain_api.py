import requests

API_URL = "http://localhost:8080/api/blockchain/transacoes"

def listar_transacoes():
    try:
        response = requests.get(API_URL)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"Erro ao buscar transações: {e}")
        return []
