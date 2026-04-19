# Package: `com.mifica.dto`

## Papel na arquitetura
Definir o **contrato externo da API** e os modelos de transporte entre camadas, desacoplando HTTP do modelo JPA.

## Responsabilidades
- Modelar payloads de request/response.
- Estabilizar integração com frontend/clientes.
- Reduzir vazamento de detalhes internos (`entity`) para fora da aplicação.

## Classes do pacote
### Autenticação e acesso
- `AuthRequest`
- `AuthResponseDTO`
- `LoginDTO`
- `LoginRequestDTO`
- `UsuarioCadastroDTO`

### Usuário e reputação
- `UsuarioDTO`
- `ReputacaoDTO`
- `HistoricoReputacaoDTO`
- `EstatisticasDTO`

### Contratos, desafios e blockchain
- `ContratoDTO`
- `DesafioDTO`
- `BlockchainContratoDTO`
- `TransacaoBlockchainDTO`

## Limites (clean architecture)
- **Pode depender de:** validações simples e tipos Java puros.
- **Não deve depender de:** `repository` e frameworks de persistência.

## Regras e invariantes
- DTO não contém regra de negócio.
- Campos sensíveis (senha, segredo, token interno) não devem ser expostos indevidamente.
- Mudanças em DTO público devem considerar compatibilidade com consumidores.

## CDD/ICP — campos atualizados
- No fluxo de transações blockchain, clientes devem enviar apenas `destinatario` e `valor`.
- `hashTransacao` e `remetente` são considerados campos técnicos/internos para auditoria.
- Compatibilidade foi mantida sem quebra de contrato: campos internos permanecem no DTO para resposta e legado.

## Convenções de evolução
- Preferir adição de campos opcionais em vez de quebra de contrato.
- Renomeações devem ser versionadas/documentadas.

## Critérios de aceitação
- Serialização/deserialização funciona sem ambiguidade.
- Contratos de API são previsíveis e documentáveis.

## Riscos e trade-offs
- Muitos DTOs aumentam volume de código, porém melhoram desacoplamento e segurança de contrato.
