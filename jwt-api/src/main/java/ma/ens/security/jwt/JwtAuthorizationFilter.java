package ma.ens.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filtre JWT qui s'execute avant chaque requete
 * Verifie la presence et la validite du token dans le header Authorization
 * Si le token est valide, l'utilisateur est authentifie dans le contexte Spring Security
 */
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    // Prefixe standard du token dans le header HTTP
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTH_HEADER = "Authorization";

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Methode principale du filtre
     * Executee une seule fois par requete (OncePerRequestFilter)
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Recuperation du header Authorization
        String authHeader = request.getHeader(AUTH_HEADER);

        // Verification de la presence du token Bearer
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {

            // Extraction du token sans le prefixe "Bearer "
            String token = authHeader.substring(7);

            try {
                String username = jwtUtil.extractUsername(token);

                // Authentification uniquement si token valide et pas deja authentifie
                if (username != null
                        && jwtUtil.validateToken(token)
                        && SecurityContextHolder.getContext().getAuthentication() == null) {

                    logger.info("Token valide pour l'utilisateur : {}", username);

                    // Chargement des details de l'utilisateur depuis la base
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Creation de l'objet d'authentification avec les roles
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // Injection de l'authentification dans le contexte Spring Security
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (Exception e) {
                logger.warn("Erreur lors du traitement du token : {}", e.getMessage());
            }
        }

        // Passage au filtre suivant dans la chaine
        filterChain.doFilter(request, response);
    }
}