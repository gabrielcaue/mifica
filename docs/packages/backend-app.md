# Package: `com.mifica`

## Papel na arquitetura
Pacote raiz da aplicação backend. Seu papel é **inicializar o runtime Spring Boot** e ativar o scan dos demais pacotes (`config`, `controller`, `service`, etc.).

## Responsabilidades
- Fornecer o ponto de entrada da aplicação.
- Garantir bootstrap consistente entre ambientes (dev/staging/prod).
- Centralizar convenções de inicialização.

## Classes do pacote
| Classe | Responsabilidade | Observações |
|---|---|---|
| `MificaApplication` | Executa `main()` e inicia o contexto Spring | Deve permanecer enxuta, sem regra de negócio |

## Limites (clean architecture)
- **Pode depender de:** Spring Boot starter e configuração global.
- **Não deve depender de:** repositórios, controllers específicos e lógica de domínio.

## Regras e invariantes
- O startup não deve assumir estado local (arquivos, cache, DB) sem configuração explícita.
- Falhas de ambiente devem gerar erro claro no boot.

## Checklist para mudanças
- Mudança alterou apenas bootstrapping?
- Não houve inclusão de regra de negócio no `main()`?
- Logs de startup continuam claros para troubleshooting?

## Critérios de aceitação
- Aplicação inicia sem exceções em ambiente configurado.
- Contexto Spring carrega todos os beans esperados.

## Riscos e trade-offs
- Acoplamento excessivo no bootstrap dificulta evolução; manter pacote mínimo reduz risco.
