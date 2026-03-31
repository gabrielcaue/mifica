package com.mifica.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mifica.util.JwtFiltro;

/**
 * Configuração central de segurança da aplicação.
 * Define a cadeia de filtros HTTP (SecurityFilterChain), política CORS,
 * endpoints públicos vs. protegidos, e integração com o filtro JWT customizado.
 *
 * Padrão: Stateless — não mantém sessão no servidor, toda autenticação
 * é feita via token JWT no header Authorization.
 */
@Configuration
public class SecurityConfig {

    // ICP-TOTAL: 3
    // ICP-01: Classe concentra decisões transversais de segurança (CORS, autorização por rota e encadeamento de filtros).

    @Autowired
    private JwtFiltro jwtFiltro;

    @Value("${app.cors.allowed-origin-patterns:*}")
    private String allowedOriginPatterns;

    /**
     * Monta a cadeia de segurança HTTP do Spring Security.
     * - CORS habilitado com configuração customizada
     * - CSRF desabilitado (API REST stateless, sem cookies de sessão)
     * - Endpoints públicos: login, cadastro, blockchain, Swagger, Actuator
     * - Filtro JWT inserido antes do UsernamePasswordAuthenticationFilter
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // ICP-02: Matriz de autorização por endpoint define fronteiras de acesso público, usuário autenticado e admin.
        http
            // Configura CORS para permitir requisições do frontend (GitHub Pages)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Desabilita CSRF — desnecessário em APIs REST stateless com JWT
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Libera preflight OPTIONS para qualquer rota (exigido pelo CORS)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Endpoints públicos — acessíveis sem autenticação
                .requestMatchers(
                    "/api/usuarios/login",
                    "/api/usuarios/cadastro",
                    "/api/usuarios/cadastro-admin",
                    "/api/usuarios/validar-acesso-admin",
                    "/api/blockchain/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/actuator/**"   // 🔑 liberar actuator
                ).permitAll()
                .requestMatchers("/api/transacoes/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )

            .addFilterBefore(jwtFiltro, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura a política CORS da aplicação.
     * Origins permitidos são lidos da variável de ambiente CORS_ALLOWED_ORIGIN_PATTERNS,
     * permitindo configuração diferente entre dev (localhost) e prod (GitHub Pages).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // ICP-03: Origem permitida é derivada dinamicamente de configuração, com saneamento de entrada.
        CorsConfiguration configuration = new CorsConfiguration();
        // Lê origins da variável de ambiente, separados por vírgula
        configuration.setAllowedOriginPatterns(
            Arrays.stream(allowedOriginPatterns.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList()
        );
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);
        // Expõe o header Authorization para o frontend acessar o token JWT
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /** Encoder BCrypt para criptografia de senhas (fator de custo padrão: 10). */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
