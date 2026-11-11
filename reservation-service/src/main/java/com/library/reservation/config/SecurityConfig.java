package com.library.reservation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(antMatcher(HttpMethod.OPTIONS, "/**")).permitAll()
                        .requestMatchers(antMatcher("/swagger-ui.html"), antMatcher("/swagger-ui/**"),
                                antMatcher("/v3/api-docs/**")).permitAll()
                        .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(antMatcher("/actuator/health")).permitAll()
                        // Creer ou annuler une reservation : USER ou ADMIN
                        .requestMatchers(antMatcher(HttpMethod.POST, "/api/reservations")).hasAnyRole("USER", "ADMIN")
                        .requestMatchers(antMatcher(HttpMethod.PUT, "/api/reservations/**")).hasAnyRole("USER", "ADMIN")
                        // Supprimer une reservation : ADMIN seulement
                        .requestMatchers(antMatcher(HttpMethod.DELETE, "/api/reservations/**")).hasRole("ADMIN")
                        // GET ouverts ; la gateway exige deja un JWT
                        .anyRequest().permitAll())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    // Keycloak met les roles dans realm_access.roles : on les transforme en ROLE_ADMIN, ROLE_USER
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>();
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.get("roles") instanceof Collection<?> roles) {
                for (Object role : roles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }
            return authorities;
        });
        return converter;
    }
}