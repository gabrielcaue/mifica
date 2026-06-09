package com.mifica.util;

import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mifica.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import com.mifica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Serviço de geração e validação de tokens JWT (JSON Web Token).
 * Utiliza HMAC-SHA256 para assinar os tokens com a chave secreta da aplicação.
 *
 * O secret é lido da variável de ambiente JWT_SECRET (nunca hardcoded).
 * Tokens expiram em 1 hora e contêm: email, id, nome e role do usuário.
 */
@Service
public class JwtService {

    // ICP-TOTAL: 2
    // ICP-01: Serviço centraliza geração/validação de tokens com claims de identidade e autorização.

    @Value("${jwt.secret}")
    private String secret;

    /** Tempo de expiração do token: 24 horas (em milissegundos). */
    private final long expiracaoMillis = 24L * 60 * 60 * 1000;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /** Gera a chave HMAC a partir do secret para assinar/validar tokens. */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Gera um token JWT contendo os dados do usuário como claims.
     * O frontend armazena esse token e o envia no header Authorization.
     *
     * @param usuario entidade do usuário autenticado
     * @return token JWT assinado com HMAC-SHA256
     */
    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .setSubject(usuario.getEmail())   // Subject = email (identificador principal)
                .claim("id", usuario.getId())      // ID para lookup rápido no frontend
                .claim("nome", usuario.getNome())  // Nome para exibição no frontend
                .claim("role", usuario.getRole())  // Role para controle de acesso (USER/ADMIN)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiracaoMillis))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Gera token a partir do email — busca o usuário no banco para incluir claims.
     */
    public String gerarToken(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para geração de token."));
        return gerarToken(usuario);
    }

    /**
     * Valida a assinatura do token e retorna os claims (payload).
     * Lança exceção se o token for inválido, expirado ou adulterado.
     *
     * @param token JWT recebido do header Authorization
     * @return claims contendo email, id, nome e role
     */
    public Claims validarToken(String token) {
        // ICP-02: Parsing assinado protege integridade do payload e controla expiração.
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Extrai o email (subject) do token
    public String extrairEmail(String token) {
        return validarToken(token).getSubject();
    }

    // Extrai a role do token
    public String extrairRole(String token) {
        return validarToken(token).get("role", String.class);
    }

    // Verifica se o token é válido (assinatura + expiração)
    public boolean tokenValido(String token) {
        try {
            validarToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Verifica se o token está expirado
    public boolean tokenExpirado(String token) {
        Date expiration = validarToken(token).getExpiration();
        return expiration.before(new Date());
    }
}
