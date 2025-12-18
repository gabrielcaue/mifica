FROM python:3.11-slim

WORKDIR /app

# Copia apenas requirements.txt primeiro para aproveitar cache
COPY requirements.txt .

# Instala dependências
RUN pip install --no-cache-dir -r requirements.txt

# Copia o restante do código
COPY . .

# Expõe a porta padrão do Streamlit
EXPOSE 8501

# Usa CMD para rodar o Streamlit com baseUrlPath
CMD ["streamlit", "run", "app.py", "--server.port=8501", "--server.address=0.0.0.0", "--server.baseUrlPath=/streamlit"]
