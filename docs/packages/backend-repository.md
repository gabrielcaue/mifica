# Package: `com.mifica.repository`

## Papel na arquitetura
Camada de acesso a dados com Spring Data JPA. Fornece interfaces para leitura/escrita sem expor SQL para camadas superiores.

## Responsabilidades
- Persistência e consulta de entidades.
- Métodos derivados para buscas frequentes.
- Suporte transacional para serviços de domínio.

## Interfaces do pacote
- `UserRepository`, `UsuarioRepository`
- `ContratoRepository`, `TransacaoRepository`, `SolicitacaoCreditoRepository`
- `DesafioRepository`, `BadgeRepository`, `HistoricoReputacaoRepository`

## Limites (clean architecture)
- **Pode depender de:** `entity`, Spring Data JPA.
- **Não deve depender de:** `controller`, DTOs de API.

## Regras e invariantes
- Não implementar regra de negócio no repositório.
- Consultas devem ser determinísticas e aderentes ao modelo.
- Mudanças de assinatura devem considerar impacto nos serviços consumidores.

## Padrões recomendados
- Nomear métodos de busca por intenção de domínio.
- Evitar sobrecarga de métodos parecidos sem necessidade.
- Monitorar queries que possam causar N+1.

## Critérios de aceitação
- CRUD básico funcionando para entidades suportadas.
- Consultas específicas retornam dados corretos com performance aceitável.

## Riscos e trade-offs
- Excesso de query methods pode aumentar acoplamento; revisar periodicamente contratos e necessidade real.
