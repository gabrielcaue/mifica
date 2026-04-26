package com.mifica.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mifica.entity.Usuario;
import com.mifica.repository.UsuarioRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Utilitário JWT usado pelos controllers para gerar e extrair dados de tokens.
 * Complementa o JwtService — este é injetado nos controllers via @Autowired,
 * enquanto o JwtService é usado internamente pelo JwtFiltro.
 *
 * Tokens gerados aqui expiram em 24 horas e incluem a role do usuário
 * para controle de acesso no frontend e backend.
 */
@Component
public class JwtUtil {

    // ICP-TOTAL: 5-6
    // Classe moderada: Geração, extração e validação de JWT com cifragem/decifragem.
    // ICP-01: Componente centraliza geração, extração e validação de JWT com dependência de estado externo (secret + repositório).

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /** Tempo de expiração do token: 24 horas (em milissegundos). */
    private static final long EXPIRATION_TIME = 86400000;

    /** Gera a chave HMAC-SHA256 a partir do secret para assinar tokens. */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Gera token JWT para o usuário identificado pelo email.
     * Busca o usuário no banco para incluir a role no token.
     */
    // Gera token com email como subject e role como claim
    public String gerarToken(String email) {
        // ICP-02: Geração de token depende de busca de usuário e composição de claims de autorização.
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado para geração de token.");
        }

        Usuario usuario = usuarioOpt.get();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", usuario.getRole()); // ← ESSENCIAL para @PreAuthorize funcionar

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(email)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }


    // Extrai o email (subject) do token
    public String extrairEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Extrai a role do token
    public String extrairRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // Verifica se o token é válido
    public boolean tokenValido(String token) {
        // ICP-03: Validação por tentativa controla falhas de parsing/assinatura sem expor exceções ao chamador.
        try {
            extrairEmail(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Verifica se o token está expirado
    public boolean tokenExpirado(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
