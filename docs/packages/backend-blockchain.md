# Package: `com.mifica.blockchain`

## Objetivo
Implementar o caso de uso de transações blockchain com validações de perfil e limite financeiro para administradores.

## Escopo
- Inclui: gravação de transações, consulta de histórico e validação de regras de transferência.
- Não inclui: autenticação JWT (apenas consumo dos dados extraídos no controller).

## Contratos e interfaces
- Entrada: `TransacaoBlockchainDTO` com uso funcional de `destinatario` e `valor`.
- Persistência: `TransacaoBlockchainRepository` com query agregada por `remetente`.
- Saída: `TransacaoBlockchainDTO` com metadados de auditoria (`id`, `hashTransacao`, `dataTransacao`).

## Regras e invariantes
- `destinatario` é obrigatório e deve existir no cadastro de usuários.
- `valor` deve ser maior que zero.
- Usuário comum não pode transferir para admin.
- Admin pode transferir para qualquer perfil, mas com limite acumulado de **1.000.000**.
- O “desconto” do saldo do admin é feito pelo somatório histórico das operações (`SUM(valor)` por `remetente`) + valor atual.

## CDD/ICP — campos atualizados
- Frontends passaram a enviar apenas `destinatario` e `valor`.
- Campos `hashTransacao` e `remetente` continuam internos para auditoria e são preenchidos no backend.
- Regra financeira de admin foi registrada com ICP no serviço e no repositório.

## Critérios de aceitação
- Transação válida persiste com `hashTransacao` e `dataTransacao` gerados no backend.
- Quando `totalJaMovimentado + valorAtual > 1.000.000`, a API retorna erro de domínio.
- Para admin no limite, nova operação deve ser bloqueada de forma determinística.

## Dependências e integrações
- `UsuarioRepository` para validação de remetente/destinatário.
- JPA (`TransacaoBlockchainRepository`) para persistência e agregação de valores.

## Riscos e trade-offs
- Cálculo por somatório histórico é simples e confiável, mas depende da integridade do histórico persistido.
