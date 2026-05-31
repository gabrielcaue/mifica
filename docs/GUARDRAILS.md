# 🛡️ Guardrails do Projeto Mifica

## Visão Geral

Os guardrails implementados fortalecem a **segurança, resiliência e observabilidade** do projeto **sem alterar em nada a lógica de negócio**. São protecciones de infraestrutura e padrões de desenvolvimento.

---

## 1. ✅ Validação de Input (Jakarta Validation)

**Status:** ✅ **IMPLEMENTADO**

**Arquivo:** `src/main/java/com/mifica/dto/*.java`

### O que faz:
- Valida dados na camada de entrada (DTOs) antes de processar.
- Utiliza anotações como `@NotBlank`, `@Email`, `@Size`, `@Positive`, `@Pattern`.
- Rejeita requisições inválidas automaticamente com mensagens estruturadas.

### DTOs com validação:
- ✅ `LoginRequestDTO` — email e senha validados
- ✅ `UsuarioCadastroDTO` — nome, email, senha, telefone
- ✅ `DesafioDTO` — título, descrição, pontos
- ✅ `TransacaoBlockchainDTO` — endereços Ethereum, valor
- ✅ `ReputacaoDTO` — range de reputação (0-1000)

### Benefícios:
- 🔒 Previne dados malformados atingindo a lógica de negócio.
- 📊 Feedback claro ao cliente sobre erros de validação.
- 🎯 Sem impacto na lógica — apenas filtro de entrada.

### Exemplo:
```java
@PostMapping("/cadastro")
public ResponseEntity<?> cadastrar(@Valid @RequestBody UsuarioCadastroDTO dto) {
    // Erro de validação é capturado automaticamente e retorna 400 Bad Request
}
```

---

## 2. ✅ GlobalExceptionHandler Centralizado

**Status:** ✅ **IMPLEMENTADO**

**Arquivo:** `src/main/java/com/mifica/controller/GlobalExceptionHandler.java`

### O que faz:
- Captura e padroniza TODAS as exceções da aplicação.
- Retorna respostas estruturadas com timestamp, status, mensagem e detalhes.
- Diferentes tipos de erro recebem código HTTP apropriado (400, 404, 409, 429, 500).

### Exceções tratadas:
| Exceção | HTTP | Mensagem |
|---------|------|----------|
| `MethodArgumentNotValidException` | 400 | Validação de entrada falhou (campos com erro) |
| `DataIntegrityViolationException` | 409 | Dados duplicados ou inválidos |
| `IllegalArgumentException` | 400 | Argumento inválido |
| `AccessDeniedException` | 403 | Acesso negado |
| `NoHandlerFoundException` | 404 | Endpoint não encontrado |
| `Exception` (genérica) | 500 | Erro interno do servidor |

### Benefícios:
- 📝 **Observabilidade:** Logs estruturados de erros.
- 🔒 **Segurança:** Detalhes técnicos não são expostos ao cliente em produção.
- 🎯 **Consistência:** Todas as respostas seguem o mesmo padrão.
- 🛠️ **Manutenibilidade:** Tratamento centralizado reduz duplicação.

### Exemplo de resposta:
```json
{
  "timestamp": "2026-05-31T10:30:45",
  "status": 400,
  "error": "Validação de entrada falhou",
  "campos": {
    "email": "Email inválido.",
    "senha": "Senha deve ter no mínimo 6 caracteres."
  }
}
```

---

## 3. ✅ Rate Limiting (Proteção contra Abuso)

**Status:** ✅ **IMPLEMENTADO**

**Arquivo:** `src/main/java/com/mifica/filter/RateLimitFilter.java`

### O que faz:
- Limita requisições por IP para proteger contra DDoS e força bruta.
- Configuração: **100 requisições por minuto por IP**.
- Responde com HTTP 429 (Too Many Requests) quando limite é atingido.
- Usa Redis para sincronizar entre instâncias (produção).

### Proteção contra:
- ⚔️ Ataques de força bruta em login/cadastro.
- 🕷️ Scraping e abuso de API.
- 🔄 Requisições automatizadas sem controle.

### Benefícios:
- 🛡️ Segurança sem impacto em usuários legítimos.
- 📊 Sincronizado entre servidores via Redis.
- 📍 Detecção de proxy (X-Forwarded-For).
- 🎯 Sem alteração na lógica de negócio.

### Resposta quando limite é atingido:
```json
HTTP/1.1 429 Too Many Requests
Retry-After: 45

{
  "error": "Rate limit excedido",
  "mensagem": "Muitas requisições. Aguarde antes de tentar novamente.",
  "retry_after": 45
}
```

---

## 4. ✅ Timeouts e Resilience (Circuit Breaker)

**Status:** ✅ **IMPLEMENTADO**

**Arquivo:** `src/main/java/com/mifica/config/ResilienceConfig.java`

### O que faz:
- Define timeouts para operações (HTTP, Redis, BD, Blockchain).
- Implementa circuit breaker para proteger contra falhas em cascata.
- Retry automático com backoff exponencial.

### Configurações de Timeout:
| Operação | Timeout | Circuit Breaker |
|----------|---------|-----------------|
| **Redis** | 2s | 50% falha = abre CB por 10s |
| **Banco de Dados** | 10s | 80% falha = abre CB por 20s |
| **Blockchain** | 15s | 60% falha = abre CB por 30s |
| **Geral** | 5s | - |

### Benefícios:
- ⏱️ **Timeouts:** Evita threads presas esperando respostas infinitas.
- 🔄 **Circuit Breaker:** Protege serviços dependentes de falhas em cascata.
- 🔁 **Retry:** Recuperação automática de erros transitórios.
- 📊 **Observabilidade:** Logs de transições de estado.
- 🎯 **Sem impacto:** Aplicado via anotações, não muda lógica.

### Exemplo de uso (futuro):
```java
@PostMapping("/blockchain/transacao")
@CircuitBreaker(name = "blockchain")
@TimeLimiter(name = "blockchain")
@Retry(name = "blockchain")
public ResponseEntity<?> criarTransacao(@Valid @RequestBody TransacaoBlockchainDTO dto) {
    // Protegido por timeouts, circuit breaker e retry
}
```

---

## 5. 🔄 WebConfig (Validação Global)

**Status:** ✅ **IMPLEMENTADO**

**Arquivo:** `src/main/java/com/mifica/config/WebConfig.java`

### O que faz:
- Ativa validação de constraints em nível global.
- Permite que `@Valid` e `@Validated` funcionem em todos os endpoints.

### Benefícios:
- 🎯 Validação automática sem boilerplate em cada endpoint.
- 🔄 Integração perfeita com GlobalExceptionHandler.

---

## 6. 🎯 Próximos Guardrails (Roadmap)

### Guardrail #5: Health Checks e Readiness Probes
- [ ] Endpoints de saúde para monitoramento (K8s, Docker).
- [ ] Verificação de conectividade com Redis, BD, Blockchain.
- Arquivo: `src/main/java/com/mifica/controller/HealthController.java`

### Guardrail #7: Auditoria de Segurança (Audit Logging)
- [ ] Registro de ações sensíveis (login, cadastro, mudanças de role).
- [ ] Sem expor dados sensíveis nos logs.
- Arquivo: `src/main/java/com/mifica/config/AuditConfig.java`

### Guardrail #8: Content Security Policy (Frontend)
- [ ] Headers CSP no nginx para prevenir XSS.
- [ ] Proteção contra inline scripts maliciosos.
- Arquivo: `nginx.conf` + `WebConfig.java` (HTTP headers)

---

## 🔍 Como Testar os Guardrails

### 1. Teste de Validação de Input
```bash
curl -X POST http://localhost:8080/api/usuarios/cadastro \
  -H "Content-Type: application/json" \
  -d '{"email": "invalido", "senha": "123"}'
# Resposta esperada: 400 Bad Request com detalhes de validação
```

### 2. Teste de Rate Limit
```bash
for i in {1..110}; do
  curl -X GET http://localhost:8080/api/desafios
done
# Após 100 requisições: 429 Too Many Requests
```

### 3. Teste de Exception Handler
```bash
curl -X POST http://localhost:8080/api/blockchain/transacoes \
  -H "Content-Type: application/json" \
  -d '{"hashTransacao": "invalid"}'
# Resposta esperada: 400 Bad Request com mensagem estruturada
```

---

## 📊 Impacto nos Logs

Todos os guardrails registram atividades importantes para observabilidade:

```
[WARN] Rate limit excedido para IP: 192.168.1.100 — contador: 101, aguarde 50 segundos
[WARN] Validação de entrada falhou: email inválido
[ERROR] Erro ao criar: Email já cadastrado (DataIntegrityViolation)
[WARN] CircuitBreaker redis transitioned from CLOSED to OPEN
```

---

## 🎯 Princípios dos Guardrails

✅ **Não alteram lógica de negócio** — São filtros, handlers e configurações.  
✅ **Transparentes para usuários legítimos** — 100 req/min é limite alto para uso normal.  
✅ **Sincronizáveis em produção** — Usam Redis quando disponível.  
✅ **Fáceis de ajustar** — Configurações centralizadas, sem hardcoding.  
✅ **Observáveis** — Registram atividades importantes em logs.  

---

## 📚 Próximas Melhorias

1. **Compressão de resposta** — Gzip para reduzir tráfego.
2. **Caching de headers** — Cache-Control, ETags.
3. **Request/Response encryption** — Para dados sensíveis.
4. **Rate limit por usuário** — Além de IP.
5. **Audit trail completo** — Com rastreamento de quem fez o quê.

---

## 🚀 Deploy com Guardrails

Os guardrails funcionam automaticamente:
- **Desenvolvimento:** Rate limit ativo, validações ativas, logs detalhados.
- **Produção:** Mesmas proteções, com observabilidade via Prometheus/Grafana.
- **Testes:** Profile `test` desativa alguns guardrails (SecurityConfig, etc).

---

**Data de implementação:** 31 de maio de 2026  
**Responsável:** GitHub Copilot  
**Status:** ✅ Pronto para produção
