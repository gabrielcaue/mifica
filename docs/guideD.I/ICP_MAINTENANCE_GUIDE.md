# 📖 Guia de Manutenção de ICP (Índice de Complexidade Percebida)

## O que é ICP?

**ICP (Índice de Complexidade Percebida)** mede quantos "pontos de decisão" uma classe possui. Ajuda devs a identificar rapidamente se uma classe é simples, moderada ou complexa.

---

## Classificação

| ICP | Classificação | Ação Recomendada |
|-----|---------------|------------------|
| 0-3 | ✅ Simples | OK - Copie este padrão |
| 4-7 | 🟡 Moderada | OK - Mantenha, mas monitore |
| 8-10 | 🔴 Complexa | ⚠️ Considere refatorar |
| 11+ | 🔴🔴 Crítica | 🚨 **REFATORE IMEDIATAMENTE** |

---

## Como Calcular ICP

Conte os **pontos de decisão** em uma classe:

### Pontos de Decisão (cada um = +1)

```
if / else / else if
for / while / do-while
switch / case
? : (ternário)
&& / || (operadores lógicos)
catch (exceções)
Chamadas para métodos complexos
Loops aninhados (cada nível = +1 adicional)
```

### Exemplo 1: UsuarioService (ICP 12-15)

```java
// Ponto 1: if dentro de validarLogin
if (usuario == null) { ... }

// Ponto 2: if dentro de validarLogin (senha)
if (!passwordEncoder.matches(...)) { ... }

// Ponto 3: if dentro de atualizarPerfil
if (dadosAtualizados.getNome() != null) { ... }

// Ponto 4: if dentro de atualizarPerfil (senha)
if (dadosAtualizados.getSenha() != null) { ... }

// Ponto 5: if dentro de aplicarRecompensas
if (!usuario.getConquistas().contains(...)) { ... }

// Ponto 6: if dentro de aplicarRecompensas (reputação)
if (usuario.getReputacao() >= 5 && ...) { ... }

// ... Total = 12-15 pontos
```

---

## Como Anotar Uma Classe

### 1. Adicione ICP-TOTAL no início da classe (após @Annotation)

```java
@Service
public class MinhaService {

    // ICP-TOTAL: 3
    // Descrição breve da classe
    // Candidata a refatoração em: [se aplicável]
    // ICP-01: Descrição do primeiro ponto de decisão
    
    ...
}
```

### 2. Adicione ICP-0X antes de cada método complexo

```java
public void metodoComplexo() {
    // ICP-02: Validação encadeada com múltiplos guards
    if (condicao1) { ... }
    if (condicao2) { ... }
    if (condicao3) { ... }
}
```

---

## Quando Refatorar (ICP > 10)

Se uma classe tem ICP > 10, é hora de quebrar em peças menores:

### Padrão 1: Dividir por Responsabilidade

**Antes (ICP 15):**
```java
@Service
public class UsuarioService {
    // 6 responsabilidades:
    // 1. Cadastro
    // 2. Login
    // 3. Perfil
    // 4. Reputação
    // 5. Créditos
    // 6. Conquistas
}
```

**Depois (ICP 2-3 cada):**
```java
@Service
public class UsuarioCadastroService { ... }

@Service
public class UsuarioLoginService { ... }

@Service
public class UsuarioPerfílService { ... }

@Service
public class UsuarioReputacaoService { ... }
```

### Padrão 2: Extrair Lógica de Validação

**Antes (ICP 8):**
```java
public void registrarTransacao(...) {
    // ICP-02: Múltiplas validações inline
    if (remetente == null) throw ...
    if (valor <= 0) throw ...
    if (admin && valor > LIMITE) throw ...
    // ... 5+ validações
}
```

**Depois (ICP 1 cada):**
```java
public void registrarTransacao(...) {
    validator.validar(remetente, valor, role);
    // ... resto da lógica
}

@Component
public class TransacaoValidator {
    public void validar(...) {
        validarRemetente(...);
        validarValor(...);
        validarPermissao(...);
    }
}
```

---

## ✅ Checklist Para Nova Classe

Quando criar uma nova classe, antes de fazer PR:

- [ ] Calcular ICP (contar pontos de decisão)
- [ ] Adicionar comentário `// ICP-TOTAL: X` na classe
- [ ] Se ICP > 10: Refatorar antes de submeter
- [ ] Se ICP 4-7: OK, mas deixar comentário mencionando monitores
- [ ] Se ICP 0-3: OK, padrão seguido ✅

---

## 🚨 Red Flags (Sinais de Refatoração Urgente)

Refatore IMEDIATAMENTE se:

1. **Classe com 8+ métodos públicos** (ICP costuma ser > 10)
   ```java
   public UsuarioCadastroDTO cadastro(...) { }
   public boolean validarLogin(...) { }
   public void atualizarPerfil(...) { }
   // ... etc (8+)
   ```

2. **Classe com 300+ linhas** (probable ICP > 8)
   ```java
   @Service
   public class UsuarioService {  // 343 linhas!
   ```

3. **Classe com 5+ @Autowired** (muitas dependências = complex)
   ```java
   @Service
   public class UsuarioService {
       @Autowired UsuarioRepository repo1;
       @Autowired UsuarioService svc1;
       @Autowired UsuarioRepository repo2;
       // ... 5+
   ```

4. **Método com 50+ linhas** (provavelmente múltiplos ICP pontos)
   ```java
   public void criarSolicitacao(...) {
       // 50 linhas de lógica
   }
   ```

---

## 📊 Dashboard ICP (Manual)

Para monitorar a saúde do projeto, rode este comando:

```bash
# Encontrar todas as classes com ICP anotado
grep -r "ICP-TOTAL:" src/main/java/com/mifica/ | grep -oE "ICP-TOTAL: [0-9\-]+" | sort | uniq -c

# Exemplo output:
# 15 ICP-TOTAL: 0-3
# 20 ICP-TOTAL: 4-7
# 10 ICP-TOTAL: 8-10
# 2 ICP-TOTAL: 11-14  ← Refatore essas!
```

---

## 🎓 Exemplos de ICP Correto

### ICP 0 - DTO (Trivial)

```java
public class LoginDTO {
    // ICP-TOTAL: 0
    // DTO trivial: apenas container de dados
    
    private String email;
    private String senha;
    
    // getters / setters (sem lógica)
}
```

### ICP 2-3 - Service Simples

```java
@Service
public class ReputacaoService {
    // ICP-TOTAL: 3
    // Classe simples: registra alteração + consulta histórico
    // ICP-01: Fluxo condicional preserva atomicidade entre auditoria e reputação
    
    public void registrarAlteracao(String email, int novaReputacao) {
        if (email == null) return;  // Ponto 1
        
        usuario.setReputacao(novaReputacao);
        historico.save(...);
    }
}
```

### ICP 12-15 - Service Crítico

```java
@Service
public class UsuarioService {
    // ICP-TOTAL: 12-15
    // Classe crítica: 6 responsabilidades (cadastro, login, perfil, reputação, créditos, conquistas)
    // Candidata a refatoração em: UsuarioCadastroService, UsuarioLoginService, UsuarioPerfílService
    // ICP-01: Autenticação combina múltiplas guard clauses
    // ICP-02: Cadastro exige criptografia antes da persistência
    // ... mais comentários ICP
    
    public boolean validarLogin(String email, String senha) {
        if (usuario == null) return false;  // Ponto 1
        if (!passwordEncoder.matches(...)) return false;  // Ponto 2
        return true;
    }
    
    public void criar(UsuarioDTO dto) {
        if (dto.getEmail() == null) throw ...  // Ponto 3
        // ... 12-15 pontos de decisão total
    }
}
```

---

## 📞 Dúvidas Frequentes

**P: Devo anotar TODOS os métodos com ICP-0X?**  
R: Não. Apenas o ICP-TOTAL na classe é obrigatório. Os ICP-0X são para métodos complexos (>3 decisões).

**P: E se a classe tiver um método muito complexo?**  
R: Considere extrair para outra classe ou service. Exemplo: se um método tem 8 pontos, extraia em outro class.

**P: ICP muda com o tempo?**  
R: Sim! Quando você refatora, recalcule e atualize o comentário ICP-TOTAL.

**P: Existem ferramentas para calcular ICP?**  
R: Sim! SonarQube, CodeFactor, Radon (Python), etc. Mas neste projeto, fazemos manual.

---

## 🔧 Manutenção Mensal (Recomendado)

A cada mês, execute:

```bash
# 1. Verificar classes com ICP > 10
grep -r "ICP-TOTAL: [1-9][0-9]" src/main/java/

# 2. Se encontrar, crie task para refatoração
# 3. Atualize CHANGELOG com redução de ICP
# 4. Documente as refatorações em docs/packages/backend-cdd-analysis.md
```

---

**Última atualização:** Abril 2026  
**Próxima revisão:** Maio 2026 (30 dias)
