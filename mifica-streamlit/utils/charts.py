import plotly.express as px
import pandas as pd

def grafico_reputacao(usuarios):
    df = pd.DataFrame(usuarios)
    fig = px.bar(df, x="nome", y="reputacao", title="Reputação dos Usuários", color="nome")
    return fig
