# Package: `com.mifica.service`

## Objetivo
Executar casos de uso e regras de negócio do domínio.

## Escopo
- Inclui: autenticação, reputação, gamificação, transações, contratos e verificação de email.
- Não inclui: roteamento HTTP e detalhes de serialização de resposta.

## Contratos e interfaces
- Métodos orientados a casos de uso consumidos por controllers.
- Integração com repositories, email e pub/sub.

## Regras e invariantes
- Regras de domínio devem estar centralizadas aqui.
- Senhas devem ser persistidas somente em hash.
- Alterações críticas devem manter consistência de reputação e estado de conta.

## Critérios de aceitação
- Fluxos de cadastro/login/perfil funcionam ponta a ponta.
- Regras de reputação e conquistas são aplicadas conforme critérios definidos.

## Dependências e integrações
- `repository`, `entity`, `dto`, `redis`, `util`.

## Riscos e trade-offs
- Classe de serviço muito grande pode perder coesão; mitigado por separação por domínio.
