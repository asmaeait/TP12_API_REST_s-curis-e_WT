package ma.ens.security.config;

import ma.ens.security.jwt.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration principale de la securite
 * Mode stateless : aucune session cote serveur, le JWT suffit
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthorizationFilter jwtFilter,
                          UserDetailsService userDetailsService) {
        this.jwtFilter = jwtFilter;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Encodeur BCrypt pour securiser les mots de passe
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * Fournisseur d'authentification qui utilise notre service
     * et BCrypt pour verifier les mots de passe
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Gestionnaire d'authentification expose comme Bean
     * Utilise par AuthController pour authentifier les utilisateurs
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Chaine de filtres de securite
     * - CSRF desactive car API REST stateless
     * - Pas de session : STATELESS
     * - Filtre JWT insere avant le filtre standard
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desactivation CSRF car API REST stateless
                .csrf(csrf -> csrf.disable())

                // Aucune session cote serveur
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Regles d'acces par URL
                .authorizeHttpRequests(auth -> auth
                        // Endpoint de login public
                        .requestMatchers("/api/auth/**").permitAll()
                        // Zone admin uniquement
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Zone utilisateur et admin
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        // Tout le reste necessite une authentification
                        .anyRequest().authenticated()
                )

                // Insertion du filtre JWT avant le filtre standard
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}