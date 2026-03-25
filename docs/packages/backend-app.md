# Package: `com.mifica`

## Objetivo
Inicializar a aplicação Spring Boot e carregar o contexto principal.

## Escopo
- Inclui: classe de bootstrap e inicialização do runtime.
- Não inclui: regras de negócio.

## Contratos e interfaces
- Inicia aplicação via `main()` e autoconfiguração Spring.

## Regras e invariantes
- A aplicação deve subir sem dependência de estado local fora de configuração.

## Critérios de aceitação
- Aplicação sobe sem erro e expõe endpoints configurados.

## Dependências e integrações
- Spring Boot autoconfiguration.

## Riscos e trade-offs
- Alto acoplamento no startup quando variáveis de ambiente faltam.
