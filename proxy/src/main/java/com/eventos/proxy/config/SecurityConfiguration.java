package com.eventos.proxy.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.nimbusds.jose.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Seguridad del Proxy.
 *
 * Requisito de consigna: la comunicación entre servicios debe estar autenticada con JWT.
 *
 * Este proxy valida JWT emitidos por el backend (JHipster) usando HS512 y el mismo base64-secret.
 */
@Configuration
public class SecurityConfiguration {

    private static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Bean
    @ConditionalOnProperty(prefix = "proxy.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz ->
                authz
                    .requestMatchers("/actuator/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                    .requestMatchers("/api/**").authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return http.build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "proxy.security", name = "enabled", havingValue = "false")
    public SecurityFilterChain noSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "proxy.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    public JwtDecoder jwtDecoder(@Value("${backend.jwt.base64-secret}") String jwtKeyBase64) {
        SecretKey secretKey = getSecretKey(jwtKeyBase64);
        return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(JWT_ALGORITHM).build();
    }

    private SecretKey getSecretKey(String jwtKeyBase64) {
        byte[] keyBytes = Base64.from(jwtKeyBase64).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JWT_ALGORITHM.getName());
    }
}


