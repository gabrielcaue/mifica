import json

def carregar_configuracoes(path="data/config.json"):
    with open(path, encoding="utf-8") as f:
        return json.load(f)
